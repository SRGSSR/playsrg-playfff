package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.RecommendedList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    public void getRecommendationRTSVideo() throws Exception {
        String mediaURN = "urn:rts:video:9691670";

        getRecommendation(mediaURN);
    }

    @Test
    public void getRecommendationRTSVAudioFull() throws Exception {
        String mediaURN = "urn:rts:audio:9866170";

        getRecommendation(mediaURN);
    }

    @Test
    public void getRecommendationRTSVAudioClip() throws Exception {
        String mediaURN = "urn:rts:audio:10163388";

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
    public void getRecommendationRTSVideoURNFormat() throws Exception {
        String mediaURN = "urn:rts:video:9691670";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationRTSAudioFullURNFormat() throws Exception {
        String mediaURN = "urn:rts:audio:9866170";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationRTSAudioClipURNFormat() throws Exception {
        String mediaURN = "urn:rts:audio:10163388";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationSRFVideoURNFormat() throws Exception {
        String mediaURN = "urn:srf:video:859dc7e6-a155-41da-9d34-8f4eb800f73c";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationSRFAudioURNFormat() throws Exception {
        String mediaURN = "urn:srf:audio:e4a1378c-db5f-4f96-a15b-ac32043c4440";

        getRecommendationURNFormat(mediaURN, true);
    }

    @Test
    public void getRecommendationSwisstxtSRFURNFormat() throws Exception {
        String mediaURN = "urn:swisstxt:video:srf:794403";

        getRecommendationURNFormat(mediaURN, false);
    }

    @Test
    public void getRecommendationSwisstxtRTSURNFormat() throws Exception {
        String mediaURN = "urn:swisstxt:video:rts:794404";

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

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN)).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false")).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true")).andExpect(status().isOk()).andExpect(jsonPath("$").isMap());

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN)).andExpect(status().isOk()).andExpect(jsonPath("$.urns[0]").value(mediaURN));
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false")).andExpect(jsonPath("$.urns[0]").value(mediaURN));
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true")).andExpect(jsonPath("$.urns[0]").value(mediaURN));

        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN)).andExpect(status().isOk()).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "false")).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());
        mvc.perform(get("/api/v2/playlist/recommendation/" + purpose + "/" + mediaURN).param("standalone", "true")).andExpect(isAvailable ? jsonPath("$.recommendationId").isNotEmpty() : jsonPath("$.recommendationId").doesNotExist());

    }

    @Test
    public void getRTSPersonalRecommendation() throws Exception {
        AtomicReference<RecommendedList> anonymousRecommendedListReference = new AtomicReference<>();
        mvc.perform(get("/api/v2/playlist/personalRecommendation")).andExpect(status().isOk()).andExpect(jsonPath("$").isMap()).andExpect(jsonPath("$.recommendationId").isNotEmpty()).andExpect(jsonPath("$.recommendationId").isNotEmpty()).andDo(mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            RecommendedList anonymousRecommendedList = (RecommendedList) convertJSONStringToObject(json, RecommendedList.class);
            anonymousRecommendedListReference.set(anonymousRecommendedList);
        });;
        mvc.perform(get("/api/v2/playlist/personalRecommendation").param("userId", "203656")).andExpect(status().isOk()).andExpect(jsonPath("$").isMap()).andExpect(jsonPath("$.recommendationId").isNotEmpty()).andExpect(jsonPath("$.title").isNotEmpty()).andDo(mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            RecommendedList userRecommendedList = (RecommendedList) convertJSONStringToObject(json, RecommendedList.class);

            Assert.assertNotEquals(anonymousRecommendedListReference.get().getRecommendationId(), userRecommendedList.getRecommendationId());
            Assert.assertNotEquals(anonymousRecommendedListReference.get().getTitle(), userRecommendedList.getTitle());
        });
    }

    public static <T>  Object convertJSONStringToObject(String json, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, objectClass);
    }
}
