package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.service.DeepLinkService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeepLinkServiceTests {

    @Autowired
    private DeepLinkService deepLinkService;

    @Test
    public void getParsePlayUrlContentTest() {
        DeepLinkJSContent deepLinkJSContent = deepLinkService.getParsePlayUrlJSContent();

        Assert.assertNotNull(deepLinkJSContent.getContentV1());
        Assert.assertNotNull(deepLinkJSContent.getHashV1());
        Assert.assertTrue(deepLinkJSContent.getContentV1().contains(deepLinkJSContent.getHashV1()));
        Assert.assertFalse(deepLinkJSContent.getContentV1().contains("INJECT TVTOPICS OBJECT"));

        Assert.assertNotNull(deepLinkJSContent.getContentV2());
        Assert.assertNotNull(deepLinkJSContent.getHashV2());
        Assert.assertTrue(deepLinkJSContent.getContentV2().contains(deepLinkJSContent.getHashV2()));
        Assert.assertFalse(deepLinkJSContent.getContentV2().contains("INJECT TVTOPICS OBJECT"));
    }

    @Test
    public void refreshParsePlayUrlContentTest() {
        DeepLinkJSContent deepLinkJSContent = deepLinkService.refreshParsePlayUrlJSContent();

        Assert.assertNotNull(deepLinkJSContent.getContentV1());
        Assert.assertNotNull(deepLinkJSContent.getHashV1());
        Assert.assertTrue(deepLinkJSContent.getContentV1().contains(deepLinkJSContent.getHashV1()));
        Assert.assertFalse(deepLinkJSContent.getContentV1().contains("INJECT TVTOPICS OBJECT"));

        Assert.assertNotNull(deepLinkJSContent.getContentV2());
        Assert.assertNotNull(deepLinkJSContent.getHashV2());
        Assert.assertTrue(deepLinkJSContent.getContentV2().contains(deepLinkJSContent.getHashV2()));
        Assert.assertFalse(deepLinkJSContent.getContentV2().contains("INJECT TVTOPICS OBJECT"));
    }
}
