package ch.srgssr.playfff.controller;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeeplinkIntegrationTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getParsePlayUrl() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/api/v1/deeplink/parse_play_url.js"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", IsNull.notNullValue()))
                .andExpect(content().string(IsNull.notNullValue())).andReturn();
        String eTag = mvcResult.getResponse().getHeader("ETag");

        mvc.perform(get("/api/v1/deeplink/parse_play_url.js").header("If-None-Match", eTag))
                .andExpect(status().isNotModified())
                .andExpect(content().string(""));
    }

    @Test
    public void reportSave() throws Exception {
        mvc.perform(post("/api/v1/deeplink/report")).andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotAcceptable());

        String nonAcceptableJson1 = "{\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(nonAcceptableJson1)).andExpect(status().isNotAcceptable());

        String nonAcceptableJson2 = "{\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(nonAcceptableJson2)).andExpect(status().isNotAcceptable());

        String nonAcceptableJson3 = "{\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(nonAcceptableJson3)).andExpect(status().isNotAcceptable());

        String acceptableJson1 = "{\n" +
                "\t\"clientTime\": \"2019-07-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.count").value(1));

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.count").value(2));

        String acceptableJson2 = "{\n" +
                "\t\"clientTime\": \"2019-07-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown2\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson2)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").value(2)).andExpect(jsonPath("$.count").value(1));

    }
}
