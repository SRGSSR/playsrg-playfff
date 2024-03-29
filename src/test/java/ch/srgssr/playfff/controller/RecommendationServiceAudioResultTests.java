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
public class RecommendationServiceAudioResultTests {

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
    public void audioFullLengthAndShortPodcastForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:1";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:2", "urn:rts:audio:3");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:1";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:2", "urn:rts:audio:3", "urn:rts:audio:4");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndShortPodcastForSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:2";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:1");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:2";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:4", "urn:rts:audio:1");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioFullLengthforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:3";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:4", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndShortPodcastForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:3";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:1", "urn:rts:audio:2");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:4";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndShortPodcastForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:0";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:1", "urn:rts:audio:2", "urn:rts:audio:3");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:0";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:4", "urn:rts:audio:3", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioClipAndShortPodcastForOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:11";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioClipforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:11";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioClipAndShortPodcastForThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:31";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:32", "urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioClipforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:31";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioClipAndShortPodcastForNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:32";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioClipforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:42";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:41", "urn:rts:audio:32", "urn:rts:audio:31", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    @Test
    public void audioClipAndShortPodcastForNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:01";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32");

        testAudioRecommendation(urn,true, expectedUrns);
    }

    @Test
    public void audioClipforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:01";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:42", "urn:rts:audio:41", "urn:rts:audio:32", "urn:rts:audio:31", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn,false, expectedUrns);
    }

    private void testAudioRecommendation(String urn, boolean isShortPodcast, List<String> expectedUrns) throws URISyntaxException {
        String mediaFileName = urn.replace(":", "-") + ".json";
        String mediaJson = BaseResourceString.getString(applicationContext, mediaFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/media/byUrn/"+ urn)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mediaJson)
                );

        String episodeCompositionFileName = isShortPodcast ? "episode-composition-rts-radio-short-podcast.json" : "episode-composition-rts-radio.json";
        String episodeCompositionJson = BaseResourceString.getString(applicationContext, episodeCompositionFileName);
        mockServer.expect(ExpectedCount.times(2),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/episodeComposition/latestByShow/byUrn/urn:rts:show:radio:1234?pageSize=100")))
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

        String expectedRecommendationId = "ch.srgssr.playfff:EpisodeComposition/LatestByShow/urn:rts:show:radio:1234";

        RecommendedList recommendedList1 = recommendationService.getRecommendedUrns("continuousplayback", urn, false);
        Assert.assertNotNull(recommendedList1);
        Assert.assertEquals(expectedRecommendationId, recommendedList1.getRecommendationId());
        Assert.assertEquals(expectedUrns, recommendedList1.getUrns());

        RecommendedList recommendedList2 = recommendationService.getRecommendedUrns("continuousplayback", urn, true);
        Assert.assertNotNull(recommendedList2);
        Assert.assertEquals(expectedRecommendationId, recommendedList2.getRecommendationId());
        Assert.assertEquals(expectedUrns, recommendedList2.getUrns());

        mockServer.verify();
    }
}
