package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeepLinkReport;
import ch.srgssr.playfff.repository.DeepLinkReportRepository;

import java.nio.charset.StandardCharsets;
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
    public void getParsePlayUrlV1() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/api/v1/deeplink/parsePlayUrl.js"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", new MediaType("application", "javascript", StandardCharsets.UTF_8).toString()))
                .andExpect(header().string("ETag", IsNull.notNullValue()))
                .andExpect(content().string(IsNull.notNullValue())).andReturn();
        String eTag = mvcResult.getResponse().getHeader("ETag");

        mvc.perform(get("/api/v1/deeplink/parsePlayUrl.js").header("If-None-Match", eTag))
                .andExpect(status().isNotModified())
                .andExpect(content().string(""));
    }

    @Test
    public void getParsePlayUrlV1Android() throws Exception {
        MvcResult mvcResult = mvc.perform(get("//api/v1/deeplink/parsePlayUrl.js"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", new MediaType("application", "javascript", StandardCharsets.UTF_8).toString()))
            .andExpect(header().string("ETag", IsNull.notNullValue()))
            .andExpect(content().string(IsNull.notNullValue())).andReturn();
        String eTag = mvcResult.getResponse().getHeader("ETag");

        mvc.perform(get("//api/v1/deeplink/parsePlayUrl.js").header("If-None-Match", eTag))
            .andExpect(status().isNotModified())
            .andExpect(content().string(""));
    }

    @Test
    public void getParsePlayUrlV2() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/api/v2/deeplink/parsePlayUrl.js"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", new MediaType("application", "javascript", StandardCharsets.UTF_8).toString()))
                .andExpect(header().string("ETag", IsNull.notNullValue()))
                .andExpect(content().string(IsNull.notNullValue())).andReturn();
        String eTag = mvcResult.getResponse().getHeader("ETag");

        mvc.perform(get("/api/v2/deeplink/parsePlayUrl.js").header("If-None-Match", eTag))
                .andExpect(status().isNotModified())
                .andExpect(content().string(""));
    }

    @Test
    public void getParsePlayUrlV2Android() throws Exception {
        MvcResult mvcResult = mvc.perform(get("//api/v2/deeplink/parsePlayUrl.js"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", new MediaType("application", "javascript", StandardCharsets.UTF_8).toString()))
            .andExpect(header().string("ETag", IsNull.notNullValue()))
            .andExpect(content().string(IsNull.notNullValue())).andReturn();
        String eTag = mvcResult.getResponse().getHeader("ETag");

        mvc.perform(get("//api/v2/deeplink/parsePlayUrl.js").header("If-None-Match", eTag))
            .andExpect(status().isNotModified())
            .andExpect(content().string(""));
    }

    @Test
    public void reportNotAcceptable() throws Exception {
        mvc.perform(post("/api/v1/deeplink/report")).andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotAcceptable());

        DeepLinkReport deepLinkReport = new DeepLinkReport();
        String deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.jsVersion = 14;
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-08-20T16:15:53+02:00");
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 0;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);
        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void reportPosted() throws Exception {
        DeepLinkReport deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown1";
        String deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);

        MvcResult mvcResult = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        DeepLinkReport deepLinkReportResult1 = JsonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), DeepLinkReport.class);

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-08-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown1";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);

        mvcResult = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(2)).andReturn();
        DeepLinkReport deepLinkReportResult2 = JsonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), DeepLinkReport.class);

        Assert.assertEquals(deepLinkReportResult1.id, deepLinkReportResult2.id);

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown2";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);

        mvcResult = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        DeepLinkReport deepLinkReportResult3 = JsonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), DeepLinkReport.class);

        Assert.assertNotEquals(deepLinkReportResult1.id, deepLinkReportResult3.id);
    }

    @Test
    @WithMockUser(username = "deepLink", password = "password", roles = "USER")
    public void reportAdmin() throws Exception {
        DeepLinkReport deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown1";
        String deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);

        MvcResult mvcResult = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isCreated()).andReturn();
        DeepLinkReport deepLinkReportResult = JsonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), DeepLinkReport.class);

        mvc.perform(get("/api/v1/deeplink/report/" + deepLinkReportResult.id)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json(deepLinkReportJson));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));

        deepLinkReport = new DeepLinkReport();
        deepLinkReport.clientTime = dateFormat.parse("2019-07-20T16:15:53+02:00");
        deepLinkReport.clientId = "ch.rts.rtsplayer";
        deepLinkReport.jsVersion = 14;
        deepLinkReport.url = "https://www.rts.ch/rts/play/unknown2";
        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReport);

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(deepLinkReportJson)).andExpect(status().isCreated());

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(delete("/api/v1/deeplink/report/" + deepLinkReportResult.id)).andExpect(status().isForbidden());

        deepLinkReportJson = JsonUtil.getMapper().writeValueAsString(deepLinkReportResult);
        mvc.perform(delete("/api/v1/deeplink/report/" + deepLinkReportResult.id).with(csrf())).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json(deepLinkReportJson));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void notAuthenticated() throws Exception {
        mvc.perform(get("/api/v1/deeplink/report/1")).andExpect(status().is3xxRedirection());

        mvc.perform(delete("/api/v1/deeplink/report/1")).andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/deeplink/report/1").with(csrf())).andExpect(status().is3xxRedirection());

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().is3xxRedirection());
    }
}
