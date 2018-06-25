package com.example.pfff.repository;

import com.example.pfff.model.peach.RecommendationResult;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class RecommendationService {

    private RestTemplate restTemplate;

    public RecommendationService() {
        restTemplate = new RestTemplate();
    }

    public List<String> getRecommendedUrns(String purpose, String urn, Boolean standalone) {
        String url = "https://peach.ebu.io/api/v1/chrts/continuous_playback_mobile?urn=" + urn + "&pageSize=50";

        return restTemplate.exchange(url, HttpMethod.GET, null, RecommendationResult.class).getBody().getUrns();
    }
}