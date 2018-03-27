package com.example.pfff.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateIntegrationTest {
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
    public void updateChange() throws Exception {
        mvc.perform(get("/update/admin")).andExpect(status().isOk());

        mvc.perform(post("/update/admin").param("package", "package.A.B").param("version", "0.1.2").param("mandatory", "false").param("text", "").with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));

        mvc.perform(get("/api/v1/update/check").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"\", \"type\":\"Optional\"}"));

        mvc.perform(post("/update/admin").param("package", "package.A.B").param("version", "0.1.2").param("text", "test012OK").param("mandatory", "true").with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));

        mvc.perform(get("/api/v1/update/check").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"test012OK\", \"type\":\"Mandatory\"}"));

        mvc.perform(get("/api/v1/update/check").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"test012OK\"}"));

        mvc.perform(post("/update/remove").param("package", "package.A.B").param("version", "0.1.2").param("mandatory", "false").with(csrf())).andExpect(status().isOk()).andExpect(content().string("removed"));

        mvc.perform(get("/api/v1/update/check").param("package", "package.A.B").param("version", "0.1.2")).andExpect(status().isOk()).andExpect(content().json("{\"type\":\"None\"}"));
    }

    @Test
    @WithMockUser(username = "update", password = "password", roles = "USER")
    public void largeText() throws Exception {
        mvc.perform(post("/update/admin").param("package", "package.A.B").param("version", "0.1.2").param("text", Utils.LARGE_TEXT).param("mandatory", "true").with(csrf())).andExpect(status().isOk()).andExpect(content().string("pushed"));
    }
}
