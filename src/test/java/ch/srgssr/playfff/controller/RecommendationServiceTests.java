package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.RecommendedList;
import ch.srgssr.playfff.service.RecommendationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendationServiceTests {

    @Autowired
    private RecommendationService recommendationService;

    @Test
    public void getRecommendedUrnsContinuousplaybackRTSVideoTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:video:9691670";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("io.ebu.peach:"));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneRTSVideoTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:video:9691670";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("io.ebu.peach:"));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackRTSAudioFullTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:audio:9866170";
        String showURN = "urn:rts:show:radio:9835809";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("ch.srgssr.playfff:EpisodeComposition/LatestByShow/" + showURN + "/FullLength/"));
        Assert.assertTrue(recommendedList.getRecommendationId().contains(mediaURN));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneRTSAudioFullTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:audio:9866170";
        String showURN = "urn:rts:show:radio:9835809";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("ch.srgssr.playfff:EpisodeComposition/LatestByShow/" + showURN + "/FullLength/"));
        Assert.assertTrue(recommendedList.getRecommendationId().contains(mediaURN));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackRTSAudioClipTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:audio:10163388";
        String showURN = "urn:rts:show:radio:8849020";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("ch.srgssr.playfff:EpisodeComposition/LatestByShow/" + showURN + "/Clip/"));
        Assert.assertTrue(recommendedList.getRecommendationId().contains(mediaURN));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneRTSAudioClipTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:rts:audio:10163388";
        String showURN = "urn:rts:show:radio:8849020";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        Assert.assertNotNull(recommendedList.getRecommendationId());
        Assert.assertTrue(recommendedList.getRecommendationId().startsWith("ch.srgssr.playfff:EpisodeComposition/LatestByShow/" + showURN + "/Clip/"));
        Assert.assertTrue(recommendedList.getRecommendationId().contains(mediaURN));
        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackSRFTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneSRFTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        assertValidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackSwissTxtTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:swisstxt:video:rts:288208";
        boolean standalone = false;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        assertInvalidList(recommendedList);
    }

    @Test
    public void getRecommendedUrnsContinuousplaybackStandaloneSwisstxtTest() {
        String purpose = "continuousplayback";
        String mediaURN = "urn:swisstxt:video:rts:288208";
        boolean standalone = true;
        RecommendedList recommendedList = recommendationService.getRecommendedUrns(purpose, mediaURN, standalone);

        assertInvalidList(recommendedList);
    }

    @Test
    public void playHomeTestInvalidUser() {
        RecommendedList recommendedList = recommendationService.rtsPlayHomePersonalRecommendation("invalid user");

        assertValidList(recommendedList);
    }

    @Test
    public void playHomeTestUserUnknown() {
        RecommendedList recommendedList = recommendationService.rtsPlayHomePersonalRecommendation("unknown");

        assertValidList(recommendedList);
    }

    @Test
    public void playHomeTestUser9() {
        RecommendedList recommendedList = recommendationService.rtsPlayHomePersonalRecommendation("9");

        assertValidList(recommendedList);
    }

    private void assertValidList(RecommendedList recommendedList) {
        Assert.assertTrue(recommendedList.getUrns().size() > 0);
        Assert.assertTrue(recommendedList.getUrns().size() < 50);
    }

    private void assertInvalidList(RecommendedList recommendedList) {
        Assert.assertNull(recommendedList.getRecommendationId());
        Assert.assertNotNull(recommendedList.getUrns());
        Assert.assertEquals(recommendedList.getUrns().size(), 0);
    }
}
