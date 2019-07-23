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

        Assert.assertNotNull(deepLinkJSContent.getContent());
        Assert.assertNotNull(deepLinkJSContent.getHash());
        Assert.assertTrue(deepLinkJSContent.getContent().contains(deepLinkJSContent.getHash()));
    }

    @Test
    public void refreshParsePlayUrlContentTest() {
        DeepLinkJSContent deepLinkJSContent = deepLinkService.refreshParsePlayUrlJSContent();

        Assert.assertNotNull(deepLinkJSContent.getContent());
        Assert.assertNotNull(deepLinkJSContent.getHash());
        Assert.assertTrue(deepLinkJSContent.getContent().contains(deepLinkJSContent.getHash()));
        Assert.assertFalse(deepLinkJSContent.getContent().contains("INJECT TVTOPICS OBJECT"));
        Assert.assertFalse(deepLinkJSContent.getContent().contains("INJECT TVEVENTS OBJECT"));
    }
}
