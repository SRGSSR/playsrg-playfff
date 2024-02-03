package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.helper.BaseResourceString;
import ch.srgssr.playfff.model.RecommendedList;
import ch.srgssr.playfff.service.IntegrationLayerRequest;
import ch.srgssr.playfff.service.RecommendationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class RecommendationServiceLivecenterResultTests {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    @Autowired
    protected ApplicationContext applicationContext;

    private MockRestServiceServer mockServer;
    private  RestTemplate mockRestTemplate = new RestTemplate();

    private  RestTemplate savedRestTemplate;

    @Before
    public void init() {
        savedRestTemplate = integrationLayerRequest.getRestTemplate();
        integrationLayerRequest.setRestTemplate(mockRestTemplate);
        mockServer = MockRestServiceServer.createServer(mockRestTemplate);
    }

    @After
    public void finish() {
        integrationLayerRequest.setRestTemplate(savedRestTemplate);
    }

    @Test
    public void livecenterScheduledLivestreamPlayingLatestTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:11";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");

        testLivecenterRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void livecenterScheduledLivestreamPlayingOldestTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:12";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");

        testLivecenterRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void livecenterScheduledLivestreamFutureTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:13";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");

        testLivecenterRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void livecenterEpisodePlayingLatestTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:21";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:22");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:22");

        testLivecenterRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void livecenterEpisodePlayingOldestTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:22";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21");

        testLivecenterRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void livecenterEpisodePastTest() throws URISyntaxException {
        String urn = "urn:swisstxt:video:srf:23";
        List<String> expectedUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:swisstxt:video:srf:11", "urn:swisstxt:video:srf:12", "urn:swisstxt:video:srf:21", "urn:swisstxt:video:srf:22");

        testLivecenterRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    private void testLivecenterRecommendation(String urn, boolean isShortPodcast, boolean isBingeWatching, List<String> expectedUrns, List<String> expectedStandaloneUrns) throws URISyntaxException {
        String scheduledlivreamMediaListFileName = "medialist-srf-livecenter-scheduledlivream.json";
        String scheduledlivreamMediaListJson = BaseResourceString.getString(applicationContext, scheduledlivreamMediaListFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/srf/mediaList/video/scheduledLivestreams/livecenter?types=scheduled_livestream&onlyEventsWithResult=true")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(scheduledlivreamMediaListJson)
            );

        String episodeMediaListFileName = "medialist-srf-livecenter-episode.json";
        String episodeMediaListJson = BaseResourceString.getString(applicationContext, episodeMediaListFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/srf/mediaList/video/scheduledLivestreams/livecenter?types=episode&onlyEventsWithResult=true")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(episodeMediaListJson)
                );

        String expectedRecommendationId = "ch.srgssr.playfff:MediaList/Livecenter/srf";

        RecommendedList recommendedList1 = recommendationService.getRecommendedUrns("continuousplayback", urn, false);
        Assert.assertNotNull(recommendedList1);
        Assert.assertEquals(expectedRecommendationId, recommendedList1.getRecommendationId());
        Assert.assertEquals(expectedUrns, recommendedList1.getUrns());

        RecommendedList recommendedList2 = recommendationService.getRecommendedUrns("continuousplayback", urn, true);
        Assert.assertNotNull(recommendedList2);
        Assert.assertEquals(expectedRecommendationId, recommendedList2.getRecommendationId());
        Assert.assertEquals(expectedStandaloneUrns, recommendedList2.getUrns());

        mockServer.verify();
    }
}
