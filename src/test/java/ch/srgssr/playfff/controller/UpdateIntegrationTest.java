package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.repository.UpdateRepository;
import org.junit.After;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateIntegrationTest {
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UpdateRepository repository;

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
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void updateNotAcceptable() throws Exception {
        mvc.perform(post("/api/v1/update").with(csrf())).andExpect(status().isBadRequest());

        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content("{}").with(csrf())).andExpect(status().isNotAcceptable());

        Update update = new Update();
        String updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.packageName = "ch.rts";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.version = "1.0";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.text = "Cool update.";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.packageName = "ch.rts";
        update.version = "1.0";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.packageName = "ch.rts";
        update.text = "Cool update.";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());

        update = new Update();
        update.version = "1.0";
        update.text = "Cool update.";
        updateJson = JsonUtil.getMapper().writeValueAsString(update);
        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void updateAdmin() throws Exception {
        mvc.perform(get("/api/v1/update")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(0)));

        Update update = new Update();
        update.packageName = "package.A.B";
        update.version = "0.1.2";
        update.text = ".";
        update.mandatory = false;
        String updateJson = JsonUtil.getMapper().writeValueAsString(update);

        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty());

        mvc.perform(get("/api/v1/update/check").param("package", update.packageName).param("version", update.version)).andExpect(status().isOk()).andExpect(jsonPath("$.type").value("Optional")).andExpect(jsonPath("$.text").value("."));

        mvc.perform(get("/api/v1/update")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));

        update = new Update();
        update.packageName = "package.A.B";
        update.version = "0.1.2";
        update.text = "test012OK";
        update.mandatory = true;
        updateJson = JsonUtil.getMapper().writeValueAsString(update);

        MvcResult mvcResult = mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty()).andReturn();
        Update updateResult = JsonUtil.getMapper().readValue(mvcResult.getResponse().getContentAsString(), Update.class);

        mvc.perform(get("/api/v1/update/check").param("package", update.packageName).param("version", update.version)).andExpect(status().isOk()).andExpect(jsonPath("$.type").value("Mandatory")).andExpect(jsonPath("$.text").value("test012OK"));

        mvc.perform(get("/api/v1/update")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)));

        updateJson = JsonUtil.getMapper().writeValueAsString(updateResult);
        mvc.perform(delete("/api/v1/update/" + updateResult.id).with(csrf())).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(content().json(updateJson));

        mvc.perform(get("/api/v1/update/check").param("package", update.packageName).param("version", update.version)).andExpect(status().isOk()).andExpect(jsonPath("$.type").value("None"));

        mvc.perform(get("/api/v1/update")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void largeText() throws Exception {
        Update update = new Update();
        update.packageName = "package.A.B";
        update.version = "0.1.2";
        update.text = Utils.LARGE_TEXT;
        update.mandatory = true;
        String updateJson = JsonUtil.getMapper().writeValueAsString(update);

        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().isCreated()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    public void notAuthenticated() throws Exception {
        mvc.perform(get("/api/v1/update")).andExpect(status().is3xxRedirection());

        Update update = new Update();
        update.packageName = "package.A.B";
        update.version = "0.1.2";
        update.text = "test012OK";
        update.mandatory = true;
        String updateJson = JsonUtil.getMapper().writeValueAsString(update);

        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson)).andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/update").contentType(MediaType.APPLICATION_JSON).content(updateJson).with(csrf())).andExpect(status().is3xxRedirection());

        mvc.perform(get("/api/v1/update/1")).andExpect(status().is3xxRedirection());

        mvc.perform(delete("/api/v1/update/1")).andExpect(status().isForbidden());

        mvc.perform(delete("/api/v1/update/1").with(csrf())).andExpect(status().is3xxRedirection());

        mvc.perform(get("/api/v1/update/check").param("package", update.packageName).param("version", update.version)).andExpect(status().isOk()).andExpect(jsonPath("$.type").value("None"));
    }
}
