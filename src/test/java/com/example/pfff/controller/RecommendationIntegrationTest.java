package com.example.pfff.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecommendationIntegrationTest {
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
    public void getRecommendationRTS() throws Exception {
        String mediaURN = "urn:rts:video:9691670";

        getRecommendation(mediaURN);
    }

    @Test
    public void getRecommendationSRF() throws Exception {
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";

        getRecommendation(mediaURN);
    }

    public void getRecommendation(String mediaURN) throws Exception {
        String purpose = "continuousplayback";

        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN)).andExpect(status().isFound()).andExpect(content().string(""));

        checkRecommendationWithRedirect(purpose, mediaURN, "false");

        checkRecommendationWithRedirect(purpose, mediaURN, "true");
    }

    private void checkRecommendationWithRedirect(String purpose, String mediaURN, String standalone) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", standalone)).andExpect(status().is3xxRedirection()).andExpect(content().string("")).andReturn();
        String location = mvcResult.getResponse().getHeader("Location");
        System.out.println(location);
        RestTemplate restTemplate = new RestTemplate();
        MediaListResult result = restTemplate.exchange(location, HttpMethod.GET, null, MediaListResult.class).getBody();
        Assert.assertNotNull(result.mediaList);
    }

    static class MediaListResult {
        @JsonProperty("mediaList")
        List<Object> mediaList;
    }

    @Test
    public void getRecommendationRTSURNFormat() throws Exception {
        String mediaURN = "urn:rts:video:9691670";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationSRFURNFormat() throws Exception {
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";

        getRecommendationURNFormat(mediaURN, false);
    }

    private void getRecommendationURNFormat(String mediaURN, boolean isAvailable) throws Exception {
        String purpose = "continuousplayback";
        String format = "urn";

        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false").param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true").param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());

        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$[0]").value(mediaURN));
        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false").param("format", format)).andExpect(jsonPath("$[0]").value(mediaURN));
        mvc.perform(get("/api/v1/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true").param("format", format)).andExpect(jsonPath("$[0]").value(mediaURN));

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false").param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true").param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("format", format)).andExpect(status().isOk()).andExpect(jsonPath("$.urns[0]").value(mediaURN));
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false").param("format", format)).andExpect(jsonPath("$.urns[0]").value(mediaURN));
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true").param("format", format)).andExpect(jsonPath("$.urns[0]").value(mediaURN));

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("format", format)).andExpect(status().isOk()).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false").param("format", format)).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true").param("format", format)).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());

    }
}
