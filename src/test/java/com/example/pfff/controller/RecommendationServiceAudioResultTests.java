package com.example.pfff.controller;

import com.example.pfff.helper.BaseResourceString;
import com.example.pfff.model.RecommendedList;
import com.example.pfff.service.IntegrationLayerRequest;
import com.example.pfff.service.RecommendationService;
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
public class RecommendationServiceAudioResultTests {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    @Autowired
    protected ApplicationContext applicationContext;

    private MockRestServiceServer mockServer;

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(integrationLayerRequest.getRestTemplate());
    }

    @Test
    public void audioFullLengthAndNextURLforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:1";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:2", "urn:rts:audio:3", "urn:rts:audio:4");

        testAudioRecommendation(urn, true,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:1";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:2", "urn:rts:audio:3", "urn:rts:audio:4");

        testAudioRecommendation(urn, true,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndNextURLforSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:2";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:4", "urn:rts:audio:1");

        testAudioRecommendation(urn, true,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforSecondEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:2";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:4", "urn:rts:audio:1");

        testAudioRecommendation(urn, true,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndNextURLforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:3";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:4", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn, true,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:3";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:4", "urn:rts:audio:1", "urn:rts:audio:2");

        testAudioRecommendation(urn, true,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndNextURLforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:4";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:3", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn, true,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:4";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:1", "urn:rts:audio:2", "urn:rts:audio:3");

        testAudioRecommendation(urn, true,false, expectedUrns);
    }

    @Test
    public void audioFullLengthAndNextURLforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:0";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:4", "urn:rts:audio:3", "urn:rts:audio:2", "urn:rts:audio:1");

        testAudioRecommendation(urn, true,true, expectedUrns);
    }

    @Test
    public void audioFullLengthforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:0";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:1", "urn:rts:audio:2", "urn:rts:audio:3", "urn:rts:audio:4");

        testAudioRecommendation(urn, true,false, expectedUrns);
    }

    @Test
    public void audioClipAndNextURLforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:11";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42");

        testAudioRecommendation(urn, false,true, expectedUrns);
    }

    @Test
    public void audioClipforOldestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:11";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42");

        testAudioRecommendation(urn, false,false, expectedUrns);
    }

    @Test
    public void audioClipAndNextURLforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:31";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn, false,true, expectedUrns);
    }

    @Test
    public void audioClipforThirdEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:31";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42", "urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22");

        testAudioRecommendation(urn, false,false, expectedUrns);
    }

    @Test
    public void audioClipAndNextURLforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:42";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:41", "urn:rts:audio:32", "urn:rts:audio:31", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn, false,true, expectedUrns);
    }

    @Test
    public void audioClipforNewestEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:42";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32", "urn:rts:audio:41");

        testAudioRecommendation(urn, false,false, expectedUrns);
    }

    @Test
    public void audioClipAndNextURLforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:01";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:42", "urn:rts:audio:41", "urn:rts:audio:32", "urn:rts:audio:31", "urn:rts:audio:22", "urn:rts:audio:21", "urn:rts:audio:12", "urn:rts:audio:11");

        testAudioRecommendation(urn, false,true, expectedUrns);
    }

    @Test
    public void audioClipforNotFoundEpisodeTest() throws URISyntaxException {
        String urn = "urn:rts:audio:01";
        List<String> expectedUrns = Arrays.asList("urn:rts:audio:11", "urn:rts:audio:12", "urn:rts:audio:21", "urn:rts:audio:22", "urn:rts:audio:31", "urn:rts:audio:32", "urn:rts:audio:41", "urn:rts:audio:42");

        testAudioRecommendation(urn, false,false, expectedUrns);
    }

    private void testAudioRecommendation(String urn, boolean isFullLength, boolean episodeCompositionHasNextUrl, List<String> expectedUrns) throws URISyntaxException {
        String mediaFileName = urn.replace(":", "-") + ".json";
        String mediaJson1 = BaseResourceString.getString(applicationContext, mediaFileName, new HashMap<>());
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/media/byUrn/"+ urn + ".json")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mediaJson1)
                );

        String episodeCompositionFileName = episodeCompositionHasNextUrl ? "episode-compositision-radio-next-url.json" : "episode-compositision-radio.json";
        String episodeCompositionJson = BaseResourceString.getString(applicationContext, episodeCompositionFileName, new HashMap<>());
        mockServer.expect(ExpectedCount.manyTimes(),
                requestTo(new URI("http://il.srgssr.ch:80/integrationlayer/2.0/episodeComposition/latestByShow/byUrn/urn:rts:show:radio:1234.json?pageSize=100")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(episodeCompositionJson)
                );

        String expectedRecommendationId = isFullLength ? "ch.srgssr.playfff:EpisodeComposition/LatestByShow/FullLength" : "ch.srgssr.playfff:EpisodeComposition/LatestByShow/Clip";

        RecommendedList recommendedList1 = recommendationService.getRecommendedUrns("continuousplayback", urn, false);
        Assert.assertNotNull(recommendedList1);
        Assert.assertEquals(recommendedList1.getRecommendationId(), expectedRecommendationId);
        Assert.assertEquals(recommendedList1.getUrns(), expectedUrns);

        RecommendedList recommendedList2 = recommendationService.getRecommendedUrns("continuousplayback", urn, true);
        Assert.assertNotNull(recommendedList2);
        Assert.assertEquals(recommendedList2.getRecommendationId(), expectedRecommendationId);
        Assert.assertEquals(recommendedList2.getUrns(), expectedUrns);
    }
}
