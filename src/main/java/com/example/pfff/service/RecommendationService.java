package com.example.pfff.service;

import com.example.pfff.model.RecommendedList;
import com.example.pfff.model.peach.RecommendationResult;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private RestTemplate restTemplate;

    public RecommendationService() {
        restTemplate = new RestTemplate();
    }

    public RecommendedList getRecommendedUrns(String purpose, String urn, boolean standalone) {
        if (urn.contains(":rts:")) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme("http").host("peach.ebu.io").path("api/v1/chrts/continuous_playback_mobile");
            uriComponentsBuilder.queryParam("urn", urn);
            uriComponentsBuilder.queryParam("purpose", purpose);
            uriComponentsBuilder.queryParam("pageSize", 50);
            uriComponentsBuilder.queryParam("standalone", standalone);
            UriComponents url = uriComponentsBuilder.build();

            System.out.println(url.toUriString());

            RecommendationResult recommendationResult = restTemplate.exchange(url.toUriString(), HttpMethod.GET, null, RecommendationResult.class).getBody();
            return new RecommendedList(recommendationResult.getRecommendationId(), recommendationResult.getUrns());
        }
        else {
            return new RecommendedList();
        }
    }
}