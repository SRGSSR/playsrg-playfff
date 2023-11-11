package ch.srgssr.playfff.service;

import ch.srg.il.domain.v2_0.*;
import ch.srgssr.playfff.model.Environment;
import ch.srgssr.playfff.model.IlUrn;
import ch.srgssr.playfff.model.RecommendedList;
import ch.srgssr.playfff.model.peach.PersonalRecommendationResult;
import ch.srgssr.playfff.model.peach.RecommendationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ch.srg.il.domain.v2_0.Type.*;

@Service
public class RecommendationService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    private final RestTemplate restTemplate;

    private final Boolean rtsRecommendationUsed;
    private final Boolean srfRecommendationUsed;
    private final int ascendingEpisodesMax;

    public RecommendationService(
            @Value("${RTS_RECOMMENDATION_USED:true}") String rtsRecommendationUsedString,
            @Value("${SRF_RECOMMENDATION_USED:true}") String srfRecommendationUsedString,
            @Value("${ASCENDING_EPISODES_MAX:25}") int ascendingEpisodesMaxInt
    ) {
        restTemplate = new RestTemplate();
        rtsRecommendationUsed = Boolean.valueOf(rtsRecommendationUsedString);
        srfRecommendationUsed = Boolean.valueOf(srfRecommendationUsedString);
        ascendingEpisodesMax = ascendingEpisodesMaxInt;
    }

    public RecommendedList getRecommendedUrns(String purpose, String urnString, boolean standalone) {
        IlUrn urn = new IlUrn(urnString);
        if (purpose.equals("relatedContent")) {
            switch (urn.getMam()) {
                case RTS:
                    if (rtsRecommendationUsed) {
                        if (urn.getMediaType() == MediaType.VIDEO) {
                            return rtsVideoRecommendedList(purpose, urnString, standalone);
                        } else if (urn.getMediaType() == MediaType.AUDIO) {
                            return pfffRecommendedList(urnString, MediaType.AUDIO, standalone);
                        }
                    } else {
                        return pfffRecommendedList(urnString, urn.getMediaType(), standalone);
                    }
                    break;
                case SRF:
                    if (srfRecommendationUsed) {
                        return srfRecommendedList(purpose, urnString, standalone);
                    } else {
                        return pfffRecommendedList(urnString, urn.getMediaType(), standalone);
                    }
                case RSI:
                case RTR:
                case SWI:
                    return pfffRecommendedList(urnString, urn.getMediaType(), standalone);
            }
        }
        else {
            switch (urn.getMam()) {
                case RTS:
                    if (rtsRecommendationUsed) {
                        if (urn.getMediaType() == MediaType.VIDEO) {
                            return rtsVideoRecommendedList(purpose, urnString, standalone);
                        } else if (urn.getMediaType() == MediaType.AUDIO) {
                            return pfffRecommendedList(urnString, MediaType.AUDIO, standalone);
                        }
                    } else {
                        return pfffRecommendedList(urnString, urn.getMediaType(), standalone);
                    }
                    break;
                case RSI:
                case RTR:
                case SRF:
                case SWI:
                    return pfffRecommendedList(urnString, urn.getMediaType(), standalone);
            }
        }
        return new RecommendedList();
    }

    private RecommendedList pfffRecommendedList(String urn, MediaType mediaType, Boolean standalone) {
        Media media = integrationLayerRequest.getMedia(urn, Environment.PROD);
        if (media == null || media.getType() == LIVESTREAM || media.getType() == SCHEDULED_LIVESTREAM || media.getShow() == null) {
            return new RecommendedList();
        }

        String showURN = media.getShow().getUrn();
        EpisodeComposition episodeComposition = integrationLayerRequest.getEpisodeCompositionLatestByShow(showURN, null, 100, Environment.PROD);
        if (episodeComposition == null) {
            return new RecommendedList();
        }

        List<EpisodeWithMedias> episodes = episodeComposition.getList().stream()
                // Don't trust the LatestByShow episode sorting, apply a common one.
                .sorted(Comparator.comparing(EpisodeWithMedias::getPublishedDate).thenComparing(EpisodeWithMedias::getId).reversed())
                .collect(Collectors.toList());

        // Use episodes and clips with the same ascendant sorting. It's assumed that clips are ascendant sorting.
        Collections.reverse(episodes);
        List<String> fullLengthUrns = episodes.stream().map(EpisodeWithMedias::getFullLengthUrn).collect(Collectors.toList());
        List<String> clipUrns = episodes.stream().flatMap(e -> e.getMediaList().stream().filter(m -> m.getMediaType() == mediaType)).map(Media::getUrn).collect(Collectors.toList());
        clipUrns.removeAll(fullLengthUrns);

        boolean isFullLengthUrns;
        List<String> recommendationResult;

        List<String> urns;
        int index = -1;
        MediaComposition mediaComposition = null;

        if (fullLengthUrns.contains(urn)) {
            isFullLengthUrns = true;
            index = fullLengthUrns.lastIndexOf(urn);
            urns = fullLengthUrns;
        } else if (clipUrns.contains(urn)) {
            isFullLengthUrns = false;
            index = clipUrns.lastIndexOf(urn);
            urns = clipUrns;
        } else if (media.getType() == CLIP) {
            isFullLengthUrns = false;
            urns = clipUrns;
        } else {
            mediaComposition = integrationLayerRequest.getMediaComposition(urn, Environment.PROD);
            isFullLengthUrns = mediaComposition == null || mediaComposition.getSegmentUrn() == null || !urn.equals(mediaComposition.getSegmentUrn());
            urns = isFullLengthUrns ? fullLengthUrns : clipUrns;
        }

        // Take care of non-standalone video.
        String baseUrn = urn;
        if (mediaType == MediaType.VIDEO && !standalone && !isFullLengthUrns && !clipUrns.isEmpty()) {
            if (index != -1) {
                EpisodeWithMedias episode = episodes.stream().filter(e -> e.getMediaList().stream().map(Media::getUrn).collect(Collectors.toList()).contains(urn)).findFirst().orElse(null);
                index = (episode != null) ? fullLengthUrns.indexOf(episode.getFullLengthUrn()) : -1;
                baseUrn = (episode != null) ? episode.getFullLengthUrn() : urn ;
            }
            urns = fullLengthUrns;
        }
        // Take care of full length and clip not in Episode composition, specially for RSI.
        else if (index == -1 && clipUrns.isEmpty()) {
            String chapterUrn = null;
            if (mediaComposition != null) {
                chapterUrn = mediaComposition.getChapterUrn();
                index = (chapterUrn != null) ? fullLengthUrns.indexOf(chapterUrn) : -1;
            }
            if (index == -1) {
                EpisodeWithMedias episode = null;
                if (isFullLengthUrns) {
                    // Find full length with the same published date as the media
                    episode = episodes.stream().filter(e -> {
                        ZonedDateTime publishedDate = e.getMediaList().get(0).getDate();
                        return publishedDate.compareTo(media.getDate()) == 0;
                    }).findFirst().orElse(null);
                }
                else {
                    // Find full length with media published date between full length start date and end date.
                    episode = episodes.stream().filter(e -> {
                        ZonedDateTime publishedDate = e.getMediaList().get(0).getDate();
                        ZonedDateTime endPublishedDate = publishedDate.plusSeconds(e.getMediaList().get(0).getDuration() / 1000);
                        return publishedDate.compareTo(media.getDate()) <= 0 && endPublishedDate.compareTo(media.getDate()) > 0;
                    }).findFirst().orElse(null);
                }
                chapterUrn = (episode != null) ? episode.getFullLengthUrn() : null;
                index = (chapterUrn != null) ? fullLengthUrns.indexOf(chapterUrn) : -1;
            }
            baseUrn = (chapterUrn != null) ? chapterUrn : urn ;
            urns = fullLengthUrns;
        }

        // First: newest medias in date ascending order. Then:
        // - if more than `ascendingEpisodesMax` episodes exists, oldest medias in date descending order.
        // - else (show has as many or fewer than `ascendingEpisodesMax` episodes), oldest medias in date ascending order.
        if (index > -1 && index < urns.size() - 1) {
            recommendationResult = new ArrayList<>(urns.subList(index + 1, urns.size()));
            urns.removeAll(recommendationResult);
        } else {
            // Latest urn or not found urn
            recommendationResult = new ArrayList<>();
        }
        urns.remove(urn);
        urns.remove(baseUrn);

        if (episodeComposition.getList().size() > ascendingEpisodesMax) {
            Collections.reverse(urns);
            recommendationResult.addAll(urns);
        } else {
            recommendationResult.addAll(urns);
        }

        String host = "playfff.srgssr.ch";
        String recommendationId = "EpisodeComposition/LatestByShow/" + showURN;

        if (recommendationResult.size() > 49) {
            recommendationResult = recommendationResult.subList(0, 49);
        }

        return new RecommendedList(host, recommendationId, recommendationResult);
    }

    private RecommendedList rtsVideoRecommendedList(String purpose, String urn, boolean standalone) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme("https")
                .host("rts-datalab.azure-api.net").path("rts-datalab-api/continuous_playback_endscreen");
        uriComponentsBuilder.queryParam("urn", urn);
        uriComponentsBuilder.queryParam("purpose", purpose);
        uriComponentsBuilder.queryParam("pageSize", 49);
        uriComponentsBuilder.queryParam("standalone", standalone);
        UriComponents url = uriComponentsBuilder.build();

        System.out.println(url.toUriString());

        try {
            RecommendationResult recommendationResult = restTemplate
                    .exchange(url.toUriString(), HttpMethod.GET, null, RecommendationResult.class).getBody();
            return new RecommendedList(url.getHost(), recommendationResult.getRecommendationId(), recommendationResult.getUrns());
        } catch (Exception e) {
            logger.warn("{} : {}", url.toUriString(), e.getMessage());
            return new RecommendedList();
        }
    }

    public RecommendedList rtsPlayHomePersonalRecommendation(String userId) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme("https")
                .host("rts-datalab.azure-api.net").path("rts-datalab-api/play_home_personal_rec");
        uriComponentsBuilder.queryParam("user_id", userId);
        UriComponents url = uriComponentsBuilder.build();

        System.out.println(url.toUriString());

        try {
            PersonalRecommendationResult result = restTemplate
                    .exchange(url.toUriString(), HttpMethod.GET, null, PersonalRecommendationResult.class).getBody();

            return new RecommendedList(result.getTitle(), url.getHost(), result.getRecommendationId(), result.getUrns());
        } catch (Exception e) {
            logger.warn("{} : {}", url.toUriString(), e.getMessage());
            return new RecommendedList();
        }
    }

    private RecommendedList srfRecommendedList(String purpose, String urn, boolean standalone) {
        long timestamp = System.currentTimeMillis();

        Environment environment = Environment.PROD;

        MediaList mediaList = integrationLayerRequest.getRecommendedMediaList(urn, environment);
        if (mediaList == null || mediaList.getList().isEmpty()) {
            return new RecommendedList();
        }

        String recommendationId = "mediaList/recommended/byUrn/" + urn + "/" + timestamp;

        return new RecommendedList(environment.getBaseUrl(), recommendationId, mediaList.getList().stream().map((i) -> i.getUrn()).collect(Collectors.toList()));
    }
}
