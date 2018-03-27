package com.example.pfff.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReleaseNoteIntegrationTest {
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
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void changeReleaseNoteTwice() throws Exception {
        mvc.perform(get("/whatisnew/admin")).andExpect(status().isOk());

        mvc.perform(post("/whatisnew/admin").param("package", "package.A.B").param("version", "0.1.2").param("text", "").with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));

        mvc.perform(get("/api/v1/whatisnew/text").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"\"}"));

        mvc.perform(post("/whatisnew/admin").param("package", "package.A.B").param("version", "0.1.2").param("text", "test012OK").with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));

        mvc.perform(get("/api/v1/whatisnew/text").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"test012OK\"}"));
    }

    @Test
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void largeText() throws Exception {
        mvc.perform(post("/whatisnew/admin").param("package", "package.A.B").param("version", "0.1.2").param("text", Utils.LARGE_TEXT).with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));
    }
}
