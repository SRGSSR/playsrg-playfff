package ch.srgssr.playfff.service;

import ch.srg.il.domain.v2_0.*;
import ch.srg.jaxb.SrgUnmarshaller;
import ch.srgssr.playfff.model.Environment;
import org.assertj.core.util.VisibleForTesting;
import org.eclipse.persistence.oxm.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Service
public class IntegrationLayerRequest {
    private static final Logger logger = LoggerFactory.getLogger(IntegrationLayerRequest.class);
    public static final int PORT = 80;

    private RestTemplate restTemplate;

    public IntegrationLayerRequest(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public EpisodeComposition getEpisodeCompositionLatestByShow(String showURN, ZonedDateTime maxPublishedDate, int pageSize, Environment environment) {
        String path = "/integrationlayer/2.0/episodeComposition/latestByShow/byUrn/" + showURN + ".json";
        String query = "pageSize=" + pageSize;
        if (maxPublishedDate != null) {
            query += "&maxPublishedDate=" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(maxPublishedDate);
        }
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, query, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, EpisodeComposition.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    public Media getMedia(String mediaURN, Environment environment) {
        String path = "/integrationlayer/2.0/media/byUrn/" + mediaURN + ".json";
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, null, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, Media.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    public Show getShow(String showURN, Environment environment) {
        String path = "/integrationlayer/2.0/show/byUrn/" + showURN + ".json";
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, null, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, Show.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    public MediaList getRecommendedMediaList(String mediaURN, Environment environment) {
        String path = "/integrationlayer/2.0/mediaList/recommended/byUrn/" + mediaURN + ".json";
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, null, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, MediaList.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    public MediaComposition getMediaComposition(String mediaURN, Environment environment) {
        String path = "/integrationlayer/2.0/mediaComposition/byUrn/" + mediaURN + ".json";
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, null, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, MediaComposition.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    public TopicList getTopics(String bu, Environment environment) {
        String path = "/integrationlayer/2.0/" + bu + "/topicList/tv.json";
        try {
            URI uri = new URI("http", null, environment.getBaseUrl(), PORT, path, null, null);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            SrgUnmarshaller unmarshaller = new SrgUnmarshaller();
            return unmarshaller.unmarshal(responseEntity.getBody(), MediaType.APPLICATION_JSON, TopicList.class);
        } catch (Exception e) {
            logger.warn("http://{}{} : {}", environment.getBaseUrl(), path, e.getMessage());
            return null;
        }
    }

    @VisibleForTesting
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @VisibleForTesting
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
