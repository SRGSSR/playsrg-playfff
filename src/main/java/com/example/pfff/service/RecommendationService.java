package com.example.pfff.service;

import ch.srg.il.domain.v2_0.EpisodeComposition;
import ch.srg.il.domain.v2_0.EpisodeWithMedias;
import ch.srg.il.domain.v2_0.Media;
import ch.srg.il.domain.v2_0.MediaType;
import com.example.pfff.model.Environment;
import com.example.pfff.model.RecommendedList;
import com.example.pfff.model.peach.RecommendationResult;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.srg.il.domain.v2_0.Type.*;

@Service
public class RecommendationService {

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    private RestTemplate restTemplate;

    public RecommendationService() {
        restTemplate = new RestTemplate();
    }

    public RecommendedList getRecommendedUrns(String purpose, String urn, boolean standalone) {
        if (urn.contains(":rts:")) {
            if (urn.contains(":video:")) {
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme("http").host("peach.ebu.io").path("api/v1/chrts/continuous_playback_mobile");
                uriComponentsBuilder.queryParam("urn", urn);
                uriComponentsBuilder.queryParam("purpose", purpose);
                uriComponentsBuilder.queryParam("pageSize", 49);
                uriComponentsBuilder.queryParam("standalone", standalone);
                UriComponents url = uriComponentsBuilder.build();

                System.out.println(url.toUriString());

                RecommendationResult recommendationResult = restTemplate.exchange(url.toUriString(), HttpMethod.GET, null, RecommendationResult.class).getBody();
                return new RecommendedList(url.getHost(), recommendationResult.getRecommendationId(), recommendationResult.getUrns());
            }
            else if (urn.contains(":audio:")) {
                Media media = integrationLayerRequest.getMedia(urn, Environment.PROD);
                if (media == null || media.getType() == LIVESTREAM || media.getType() == SCHEDULED_LIVESTREAM || media.getShow() == null) {
                    return new RecommendedList();
                }

                EpisodeComposition episodeComposition = integrationLayerRequest.getEpisodeCompositionLatestByShow(media.getShow().getUrn(), null, 100, Environment.PROD);
                if (episodeComposition == null) {
                    new RecommendedList();
                }

                List<EpisodeWithMedias> episodes = Lists.reverse(episodeComposition.getList());
                List<String> fullLengthUrns = episodes.stream().map(EpisodeWithMedias::getFullLengthUrn).collect(Collectors.toList());
                List<String> clipUrns = episodes.stream().flatMap(e -> e.getMediaList().stream().filter(m -> m.getMediaType() == MediaType.AUDIO)).map(Media::getUrn).collect(Collectors.toList());
                clipUrns.removeAll(fullLengthUrns);

                Boolean isFullLengthUrns = false;
                List<String> recommendationResult = null;

                List<String> urns = null;
                int index = -1;

                if (fullLengthUrns.contains(urn)) {
                    isFullLengthUrns = true;
                    index = fullLengthUrns.lastIndexOf(urn);
                    urns = fullLengthUrns;
                }
                else if (clipUrns.contains(urn)) {
                    isFullLengthUrns = false;
                    index = clipUrns.lastIndexOf(urn);
                    urns = clipUrns;
                }
                else {
                    isFullLengthUrns = media.getType() != CLIP;
                    urns = isFullLengthUrns ? fullLengthUrns : clipUrns;
                }

                // First: newest medias in date ascending order. Then:
                // - if `nextUrl` exists (show has more than 100 episodes), oldest medias in date descending order.
                // - else (show has less than 100 episodes), oldest medias in date ascending order.
                if (index > -1 && index < urns.size() - 1) {
                    recommendationResult = new ArrayList<>(urns.subList(index + 1, urns.size()));
                    urns.removeAll(recommendationResult);
                    urns.remove(urn);
                }
                else {
                    // Latest urn or not found urn
                    recommendationResult = new ArrayList<>();
                }

                if (episodeComposition.getNext() != null) {
                    recommendationResult.addAll(Lists.reverse(urns));
                }
                else {
                    recommendationResult.addAll(urns);
                }

                String host = "playfff.srgssr.ch";
                String recommendationId = "EpisodeComposition/LatestByShow/" + (isFullLengthUrns ? "FullLength" : "Clip");

                if (recommendationResult.size() > 49) {
                    recommendationResult = recommendationResult.subList(0, 49);
                }

                return new RecommendedList(host, recommendationId, recommendationResult);
            }
            else {
                return new RecommendedList();
            }
        }
        else {
            return new RecommendedList();
        }
    }
}