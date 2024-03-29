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
public class RecommendationServiceVideoResultTests {

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
    public void videoFullLengthAndShortPodcastForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthAndShortPodcastForSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:1");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthForSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthForThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthAndShortPodcastForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:4";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthAndShortPodcastForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }


    @Test
    public void videoClipAndShortPodcastForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipForThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:31";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipAndShortPodcastForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:33";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:11", "urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:43";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:42", "urn:rtr:video:41", "urn:rtr:video:33", "urn:rtr:video:32", "urn:rtr:video:31", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipAndShortPodcastForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:11", "urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33");

        testVideoRecommendation(urn,true, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoClipForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:43", "urn:rtr:video:42", "urn:rtr:video:41", "urn:rtr:video:33", "urn:rtr:video:32", "urn:rtr:video:31", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn,false, false, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthAndShortPodcastWithBingeWatchingSortedForOldestEpisodeTest() throws URISyntaxException {
        // Should have same returned list as `videoFullLengthAndShortPodcastForOldestEpisodeTest`.
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3");

        testVideoRecommendation(urn,true, true, expectedUrns, expectedStandaloneUrns);
    }

    @Test
    public void videoFullLengthWithBingeWatchingSortedForOldestEpisodeTest() throws URISyntaxException {
        // Should have same returned list as `videoFullLengthForOldestEpisodeTest`.
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");
        List<String> expectedStandaloneUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn,false, true, expectedUrns, expectedStandaloneUrns);
    }

    private void testVideoRecommendation(String urn, boolean isShortPodcast, boolean isBingeWatching, List<String> expectedUrns, List<String> expectedStandaloneUrns) throws URISyntaxException {
        String mediaFileName = urn.replace(":", "-") + ".json";
        String mediaJson = BaseResourceString.getString(applicationContext, mediaFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/media/byUrn/"+ urn)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mediaJson)
                );

        String episodeCompositionFileName = isShortPodcast ? "episode-composition-rtr-tv-short-podcast" : "episode-composition-rtr-tv";
        episodeCompositionFileName += isBingeWatching ? "-binge-watching.json" : ".json";
        String episodeCompositionJson = BaseResourceString.getString(applicationContext, episodeCompositionFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/episodeComposition/latestByShow/byUrn/urn:rtr:show:tv:1234?pageSize=100")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(episodeCompositionJson)
                );

        try {
            String mediaCompositionFileName = "media-composition-" + urn.replace(":", "-") + ".json";
            String mediaCompositionJson = BaseResourceString.getString(applicationContext, mediaCompositionFileName);
            mockServer.expect(ExpectedCount.between(0, 2),
                    requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/mediaComposition/byUrn/" + urn)))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(mediaCompositionJson)
                    );
        } catch (Exception e) {}

        String expectedRecommendationId = "ch.srgssr.playfff:EpisodeComposition/LatestByShow/urn:rtr:show:tv:1234";

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
