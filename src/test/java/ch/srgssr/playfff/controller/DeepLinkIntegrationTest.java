package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeepLinkReport;
import ch.srgssr.playfff.repository.DeepLinkReportRepository;
import java.text.SimpleDateFormat;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeepLinkIntegrationTest {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DeepLinkReportRepository repository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
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
    public void reportNotAcceptable() throws Exception {
        mvc.perform(post("/api/v1/deeplink/report")).andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotAcceptable());

        DeepLinkReport notAcceptableDeepLinkReport = new DeepLinkReport();
        String notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.jsVersion = 14;
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.clientId = "ch.rts.rtsplayer";
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.clientTime = dateFormat.parse("2019-08-20T16:15:53+02:00");
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.jsVersion = 14;
        notAcceptableDeepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.clientId = "ch.rts.rtsplayer";
        notAcceptableDeepLinkReport.jsVersion = 14;
        notAcceptableDeepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());

        notAcceptableDeepLinkReport = new DeepLinkReport();
        notAcceptableDeepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        notAcceptableDeepLinkReport.clientId = "ch.rts.rtsplayer";
        notAcceptableDeepLinkReport.jsVersion = 0;
        notAcceptableDeepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        notAcceptableJson = JsonUtil.getMapper().writeValueAsString(notAcceptableDeepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(notAcceptableJson)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void reportPosted() throws Exception {
        DeepLinkReport acceptableDeepLinkReport1 = new DeepLinkReport();
        acceptableDeepLinkReport1.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        acceptableDeepLinkReport1.clientId = "ch.rts.rtsplayer";
        acceptableDeepLinkReport1.jsVersion = 14;
        acceptableDeepLinkReport1.url = "https://www.rts.ch/rts/play/unknown1";
        String acceptableJson1 = JsonUtil.getMapper().writeValueAsString(acceptableDeepLinkReport1);

        MvcResult mvcResult1 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        DeepLinkReport deepLinkReport1 = JsonUtil.getMapper().readValue(mvcResult1.getResponse().getContentAsString(), DeepLinkReport.class);

        DeepLinkReport acceptableDeepLinkReport1Bis = new DeepLinkReport();
        acceptableDeepLinkReport1Bis.clientTime = dateFormat.parse("2019-08-20T16:15:53+02:00");
        acceptableDeepLinkReport1Bis.clientId = "ch.rts.rtsplayer";
        acceptableDeepLinkReport1Bis.jsVersion = 14;
        acceptableDeepLinkReport1Bis.url = "https://www.rts.ch/rts/play/unknown1";
        String acceptableJson1bis = JsonUtil.getMapper().writeValueAsString(acceptableDeepLinkReport1Bis);

        MvcResult mvcResult1bis = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1bis)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(2)).andReturn();
        DeepLinkReport deepLinkReport1Bis = JsonUtil.getMapper().readValue(mvcResult1bis.getResponse().getContentAsString(), DeepLinkReport.class);

        Assert.assertEquals(deepLinkReport1.id, deepLinkReport1Bis.id);

        DeepLinkReport acceptableDeepLinkReport2 = new DeepLinkReport();
        acceptableDeepLinkReport2.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        acceptableDeepLinkReport2.clientId = "ch.rts.rtsplayer";
        acceptableDeepLinkReport2.jsVersion = 14;
        acceptableDeepLinkReport2.url = "https://www.rts.ch/rts/play/unknown2";
        String acceptableJson2 = JsonUtil.getMapper().writeValueAsString(acceptableDeepLinkReport2);

        MvcResult mvcResult2 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson2)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        DeepLinkReport deepLinkReport2 = JsonUtil.getMapper().readValue(mvcResult2.getResponse().getContentAsString(), DeepLinkReport.class);

        Assert.assertNotEquals(deepLinkReport1.id, deepLinkReport2.id);
    }

    @Test
    @WithMockUser(username = "deeplink", password = "password", roles = "USER")
    public void reportAdmin() throws Exception {
        DeepLinkReport acceptableDeepLinkReport1 = new DeepLinkReport();
        acceptableDeepLinkReport1.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        acceptableDeepLinkReport1.clientId = "ch.rts.rtsplayer";
        acceptableDeepLinkReport1.jsVersion = 14;
        acceptableDeepLinkReport1.url = "https://www.rts.ch/rts/play/unknown1";
        String acceptableJson1 = JsonUtil.getMapper().writeValueAsString(acceptableDeepLinkReport1);

        MvcResult mvcResult1 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1)).andExpect(status().isCreated()).andReturn();
        DeepLinkReport deepLinkReport1 = JsonUtil.getMapper().readValue(mvcResult1.getResponse().getContentAsString(), DeepLinkReport.class);

        mvc.perform(get("/api/v1/deeplink/report/" + deepLinkReport1.id)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(content().json(acceptableJson1));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));

        DeepLinkReport acceptableDeepLinkReport2 = new DeepLinkReport();
        acceptableDeepLinkReport2.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        acceptableDeepLinkReport2.clientId = "ch.rts.rtsplayer";
        acceptableDeepLinkReport2.jsVersion = 14;
        acceptableDeepLinkReport2.url = "https://www.rts.ch/rts/play/unknown2";
        String acceptableJson2 = JsonUtil.getMapper().writeValueAsString(acceptableDeepLinkReport2);

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson2)).andExpect(status().isCreated());

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(delete("/api/v1/deeplink/report/" + deepLinkReport1.id)).andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/deeplink/report/" + deepLinkReport1.id).with(csrf())).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(content().json(acceptableJson1));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void notAuthenticated() throws Exception {
        mvc.perform(get("/api/v1/deeplink/report/1")).andExpect(status().is3xxRedirection());

        mvc.perform(delete("/api/v1/deeplink/report/1")).andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/deeplink/report/1").with(csrf())).andExpect(status().is3xxRedirection());

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().is3xxRedirection());
    }
}
