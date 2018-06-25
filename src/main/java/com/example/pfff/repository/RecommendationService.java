package com.example.pfff.repository;

import com.example.pfff.model.peach.RecommendationResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.List;

@Repository
public class RecommendationService {

    private RestTemplate restTemplate;

    public RecommendationService() {
        restTemplate = new RestTemplate();
    }

    public List<String> getRecommendedUrns(String purpose, String urn, Boolean standalone) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme("http").host("peach.ebu.io").path("api/v1/chrts/continuous_playback_mobile");
        uriComponentsBuilder.queryParam("urn", urn);
        uriComponentsBuilder.queryParam("purpose", purpose);
        uriComponentsBuilder.queryParam("pageSize", 50);
        if (standalone != null) {
            uriComponentsBuilder.queryParam("standalone", standalone);
        }
        UriComponents url = uriComponentsBuilder.build();

        System.out.println(url.toUriString());

        return restTemplate.exchange(url.toUriString(), HttpMethod.GET, null, RecommendationResult.class).getBody().getUrns();
    }
}