package com.example.pfff.controller;

import com.example.pfff.service.ReleaseNoteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReleaseNoteIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void versionShouldReturnDefaultMessage() throws Exception {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/whatisnew/save")
                .queryParam("package", "package.A.B")
                .queryParam("version", "0.1.2");

        assertThat(this.restTemplate.postForObject("http://localhost:8080/whatisnew/save?package=package.A.B&version=0.1.2&text=", null, String.class)).contains("pushed");

        assertThat(this.restTemplate.getForObject("http://localhost:8080/whatisnew/text?package=package.A.B&version=0.1.2", String.class)).contains("{\"text\":\"\",\"title\":null}");

        assertThat(this.restTemplate.postForObject("http://localhost:8080/whatisnew/save?package=package.A.B&version=0.1.2&text=test012OK", null, String.class)).contains("pushed");

        assertThat(this.restTemplate.getForObject("http://localhost:8080/whatisnew/text?package=package.A.B&version=0.1.2", String.class)).contains("{\"text\":\"test012OK\",\"title\":null}");
    }
}
