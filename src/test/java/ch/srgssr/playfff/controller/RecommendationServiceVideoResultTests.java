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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
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
    public void videoFullLengthAndNextURLforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,true, false, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforOldestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,true, true, expectedUrns);
    }

    @Test
    public void videoFullLengthforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,false, false, expectedUrns);
    }

    @Test
    public void videoFullLengthforOldestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:1";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,false, true, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, false, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforSecondEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, true, expectedUrns);
    }

    @Test
    public void videoFullLengthforSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,false, false, expectedUrns);
    }

    @Test
    public void videoFullLengthforSecondEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:2";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:4", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,false, true, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, false, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforThirdEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, true, expectedUrns);
    }

    @Test
    public void videoFullLengthforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:1", "urn:rtr:video:2");

        testVideoRecommendation(urn, true,false, false, expectedUrns);
    }

    @Test
    public void videoFullLengthforThirdEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:3";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:1", "urn:rtr:video:2");

        testVideoRecommendation(urn, true,false, true, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:4";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, false, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforNewestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:4";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, true, expectedUrns);
    }

    @Test
    public void videoFullLengthforNewestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:4";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3");

        testVideoRecommendation(urn, true,false, true, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, false, expectedUrns);
    }

    @Test
    public void videoFullLengthAndNextURLforNotFoundEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, true,true, true, expectedUrns);
    }

    @Test
    public void videoFullLengthforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,false, false, expectedUrns);
    }

    @Test
    public void videoFullLengthforNotFoundEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:0";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, true,false, true, expectedUrns);
    }


    @Test
    public void videoClipAndNextURLforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, false,true, false, expectedUrns);
    }

    @Test
    public void videoClipAndNextURLforOldestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43");

        testVideoRecommendation(urn, false,true, true, expectedUrns);
    }

    @Test
    public void videoClipforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, false,false, false, expectedUrns);
    }

    @Test
    public void videoClipforOldestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:11";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43");

        testVideoRecommendation(urn, false,false, true, expectedUrns);
    }


    @Test
    public void videoClipAndNextURLforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:31";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, false,true, false, expectedUrns);
    }

    @Test
    public void videoClipAndNextURLforThirdEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:31";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn, false,true, true, expectedUrns);
    }

    @Test
    public void videoClipforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:31";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:1", "urn:rtr:video:2");

        testVideoRecommendation(urn, false,false, false, expectedUrns);
    }

    @Test
    public void videoClipforThirdEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:31";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43", "urn:rtr:video:11", "urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23");

        testVideoRecommendation(urn, false,false, true, expectedUrns);
    }


    @Test
    public void videoClipAndNextURLforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:43";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, false,true, false, expectedUrns);
    }

    @Test
    public void videoClipAndNextURLforNewestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:43";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:42", "urn:rtr:video:41", "urn:rtr:video:33", "urn:rtr:video:32", "urn:rtr:video:31", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn, false,true, true, expectedUrns);
    }

    @Test
    public void videoClipforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:43";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3");

        testVideoRecommendation(urn, false,false, false, expectedUrns);
    }

    @Test
    public void videoClipforNewestEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:43";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:11", "urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42");

        testVideoRecommendation(urn, false,false, true, expectedUrns);
    }

    @Test
    public void videoClipAndNextURLforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:4", "urn:rtr:video:3", "urn:rtr:video:2", "urn:rtr:video:1");

        testVideoRecommendation(urn, false,true, false, expectedUrns);
    }

    @Test
    public void videoClipAndNextURLforNotFoundEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:43", "urn:rtr:video:42", "urn:rtr:video:41", "urn:rtr:video:33", "urn:rtr:video:32", "urn:rtr:video:31", "urn:rtr:video:23", "urn:rtr:video:22", "urn:rtr:video:21", "urn:rtr:video:13", "urn:rtr:video:12", "urn:rtr:video:11");

        testVideoRecommendation(urn, false,true, true, expectedUrns);
    }

    @Test
    public void videoClipforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:1", "urn:rtr:video:2", "urn:rtr:video:3", "urn:rtr:video:4");

        testVideoRecommendation(urn, false,false, false, expectedUrns);
    }

    @Test
    public void videoClipforNotFoundEpisodeStandaloneTest() throws URISyntaxException {
        String urn = "urn:rtr:video:01";
        List<String> expectedUrns = Arrays.asList("urn:rtr:video:11", "urn:rtr:video:12", "urn:rtr:video:13", "urn:rtr:video:21", "urn:rtr:video:22", "urn:rtr:video:23", "urn:rtr:video:31", "urn:rtr:video:32", "urn:rtr:video:33", "urn:rtr:video:41", "urn:rtr:video:42", "urn:rtr:video:43");

        testVideoRecommendation(urn, false,false, true, expectedUrns);
    }

    private void testVideoRecommendation(String urn, boolean isFullLength, boolean episodeCompositionHasNextUrl, Boolean standalone, List<String> expectedUrns) throws URISyntaxException {
        String mediaFileName = urn.replace(":", "-") + ".json";
        String mediaJson = BaseResourceString.getString(applicationContext, mediaFileName, new HashMap<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/media/byUrn/"+ urn + ".json")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mediaJson)
                );

        String episodeCompositionFileName = episodeCompositionHasNextUrl ? "episode-composition-rtr-tv-next-url.json" : "episode-composition-rtr-tv.json";
        String episodeCompositionJson = BaseResourceString.getString(applicationContext, episodeCompositionFileName, new HashMap<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/episodeComposition/latestByShow/byUrn/urn:rtr:show:tv:1234.json?pageSize=100")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(episodeCompositionJson)
                );

        try {
            String mediaCompositionFileName = "media-composition-" + urn.replace(":", "-") + ".json";
            String mediaCompositionJson = BaseResourceString.getString(applicationContext, mediaCompositionFileName, new HashMap<>());
            mockServer.expect(ExpectedCount.between(0, 1),
                    requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/mediaComposition/byUrn/" + urn + ".json")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(mediaCompositionJson)
                    );
        } catch (Exception e) {}

        String expectedRecommendationId = isFullLength ? "ch.srgssr.playfff:EpisodeComposition/LatestByShow/urn:rtr:show:tv:1234/FullLength/" + urn : "ch.srgssr.playfff:EpisodeComposition/LatestByShow/urn:rtr:show:tv:1234/Clip/" + urn;

        RecommendedList recommendedList = recommendationService.getRecommendedUrns("continuousplayback", urn, standalone);
        Assert.assertNotNull(recommendedList);
        String recommendationId = recommendedList.getRecommendationId();
        // Remove timestamp part
        recommendationId = recommendationId.substring(0, recommendationId.lastIndexOf("/"));
        Assert.assertEquals(recommendationId, expectedRecommendationId);
        Assert.assertEquals(recommendedList.getUrns(), expectedUrns);

        mockServer.verify();
    }
}
