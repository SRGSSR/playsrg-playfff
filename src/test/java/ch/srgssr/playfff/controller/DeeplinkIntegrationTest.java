package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.ParsingReport;
import ch.srgssr.playfff.repository.ParsingReportRepository;
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
public class DeeplinkIntegrationTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ParsingReportRepository repository;

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

        MvcResult mvcResult1 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        ParsingReport parsingReport1 = JsonUtil.getMapper().readValue(mvcResult1.getResponse().getContentAsString(), ParsingReport.class);

        String acceptableJson1bis = "{\n" +
                "\t\"clientTime\": \"2019-08-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        MvcResult mvcResult1bis = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson1bis)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(2)).andReturn();
        ParsingReport parsingReport1bis = JsonUtil.getMapper().readValue(mvcResult1bis.getResponse().getContentAsString(), ParsingReport.class);

        Assert.assertEquals(parsingReport1.id, parsingReport1bis.id);

        String acceptableJson2 = "{\n" +
                "\t\"clientTime\": \"2019-07-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown2\"\n" +
                "}";

        MvcResult mvcResult2 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(acceptableJson2)).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$.id").isNotEmpty()).andExpect(jsonPath("$.count").value(1)).andReturn();
        ParsingReport parsingReport2 = JsonUtil.getMapper().readValue(mvcResult2.getResponse().getContentAsString(), ParsingReport.class);

        Assert.assertNotEquals(parsingReport1.id, parsingReport2.id);
    }

    @Test
    @WithMockUser(username = "deeplink", password = "password", roles = "USER")
    public void reportChange() throws Exception {
        String json1 = "{\n" +
                "\t\"clientTime\": \"2019-07-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown\"\n" +
                "}";

        MvcResult mvcResult1 = mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(json1)).andExpect(status().isCreated()).andReturn();
        ParsingReport parsingReport1 = JsonUtil.getMapper().readValue(mvcResult1.getResponse().getContentAsString(), ParsingReport.class);

        mvc.perform(get("/api/v1/deeplink/report/" + parsingReport1.id)).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(content().json(json1));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));

        String json2 = "{\n" +
                "\t\"clientTime\": \"2019-07-20T16:15:53+02:00\",\n" +
                "\t\"clientId\": \"ch.rts.rtsplayer.debug\",\n" +
                "\t\"jsVersion\": \"v1.0\",\n" +
                "\t\"url\": \"https:\\/\\/www.rts.ch\\/rts\\/play\\/unknown2\"\n" +
                "}";

        mvc.perform(post("/api/v1/deeplink/report").contentType(MediaType.APPLICATION_JSON).content(json2)).andExpect(status().isCreated());

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(2)));

        mvc.perform(delete("/api/v1/deeplink/report/" + parsingReport1.id)).andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/deeplink/report/" + parsingReport1.id).with(csrf())).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(content().json(json1));

        mvc.perform(get("/api/v1/deeplink/report")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));
    }
}
