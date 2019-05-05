package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeeplinkContent;
import ch.srgssr.playfff.service.DeeplinkService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeeplinkServiceTests {

    @Autowired
    private DeeplinkService deeplinkService;

    @Test
    public void getParsePlayUrlContentTest() {
        DeeplinkContent deeplinkContent = deeplinkService.getParsePlayUrlContent();

        Assert.assertNotNull(deeplinkContent.getContent());
        Assert.assertNotNull(deeplinkContent.getHash());
        Assert.assertTrue(deeplinkContent.getContent().contains(deeplinkContent.getHash()));
    }

    @Test
    public void refreshParsePlayUrlContentTest() {
        DeeplinkContent deeplinkContent = deeplinkService.refreshParsePlayUrlContent();

        Assert.assertNotNull(deeplinkContent.getContent());
        Assert.assertNotNull(deeplinkContent.getHash());
        Assert.assertTrue(deeplinkContent.getContent().contains(deeplinkContent.getHash()));
        Assert.assertFalse(deeplinkContent.getContent().contains("INJECT TVTOPICS OBJECT"));
        Assert.assertFalse(deeplinkContent.getContent().contains("INJECT TVEVENTS OBJECT"));
    }
}
