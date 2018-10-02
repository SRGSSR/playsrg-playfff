package com.example.pfff.controller;

import com.example.pfff.model.RecommendedList;
import com.example.pfff.service.RecommendationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendationServiceTests {

    @Autowired
    private RecommendationService recommendationService;

    @Test
    public void getRecommendedUrnsContinuousplaybackRTSTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:video:9691670";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneRTSTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:video:9691670";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackSRFTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneSRFTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackSwissTxtTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:swisstxt:video:rts:288208";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneSwisstxtTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:swisstxt:video:rts:288208";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
    }
}
