package ch.srgssr.playfff.controller;

import ch.srg.il.domain.v2_0.ModuleConfig;
import ch.srg.il.domain.v2_0.ModuleConfigList;
import ch.srgssr.playfff.model.Environment;
import ch.srgssr.playfff.service.IntegrationLayerRequest;
import ch.srgssr.playportal.PlayTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
@RequestMapping(value = "/deeplink")
public class DeeplinkController {

    private String cacheReponse;

    private RestTemplate restTemplate;

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    public DeeplinkController(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    @RequestMapping("/v1/parse_play_url.js")
    @ResponseBody
    public ResponseEntity<String> parsePlayUrlJavascript() throws URISyntaxException {
        URI uri = new URI("https", null, "play-mmf.herokuapp.com", 443, "/deeplink/v1/parse_play_url.js",
                null, null);

        ResponseEntity<String> jsResponseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
        if (jsResponseEntity.getStatusCode() != HttpStatus.OK) {
            return jsResponseEntity;
        }

        String javascript = jsResponseEntity.getBody();

        Map<String, String> buMap = new HashMap<String, String>();
        buMap.put("srf", "www.srf.ch");
        buMap.put("rts", "www.rts.ch");
        buMap.put("rsi", "www.rsi.ch");
        buMap.put("rtr", "www.rtr.ch");
        buMap.put("swi", "play.swissinfo.ch");

        ObjectMapper mapperObj = new ObjectMapper();

        // Get tv topic list
        Map<String, Map<String, String>> tvTopicsMap = new HashMap<>();

        for (Map.Entry<String, String> bu : buMap.entrySet()) {
            URI tvTopicListUri = new URI("https", null, bu.getValue(), 443, "/play/tv/topicList",
                    null, null);
            ResponseEntity<PlayTopic[]> tvTopicListResponseEntity = restTemplate.exchange(tvTopicListUri, HttpMethod.GET, null, PlayTopic[].class);
            if (tvTopicListResponseEntity.getBody() != null) {
                PlayTopic[] tvTopicList = tvTopicListResponseEntity.getBody();
                Map<String, String> tvTopicsSubMap = new HashMap<>();

                for (PlayTopic playTopic : tvTopicList) {
                    tvTopicsSubMap.put(playTopic.getUrlEncodedTitle(), playTopic.getId());
                }

                tvTopicsMap.put(bu.getKey(), tvTopicsSubMap);
            }
        }

        String tvTopics = null;
        try {
            tvTopics = mapperObj.writeValueAsString(tvTopicsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tvTopics != null) {
            javascript = javascript.replaceAll("\\/\\* INJECT TVTOPICS OBJECT \\*\\/", "var tvTopics = " + tvTopics);
        }

        // Get event module list
        Map<String, Map<String, String>> tvEventsMap = new HashMap<>();

        for (Map.Entry<String, String> bu : buMap.entrySet()) {
            ModuleConfigList moduleConfigList = integrationLayerRequest.getEvents(bu.getKey(), Environment.PROD);
            if (moduleConfigList != null) {
                Map<String, String> tvEventsSubMap = new HashMap<>();

                for (int i = 0; i < moduleConfigList.getModuleConfigList().size(); i++) {
                    ModuleConfig moduleConfig = moduleConfigList.getModuleConfigList().get(i);
                    tvEventsSubMap.put(moduleConfig.getSeoName(), moduleConfig.getId());
                }

                tvEventsMap.put(bu.getKey(), tvEventsSubMap);
            }
        }

        String tvEvents = null;
        try {
            tvEvents = mapperObj.writeValueAsString(tvEventsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tvEvents != null) {
            javascript = javascript.replaceAll("\\/\\* INJECT TVEVENTS OBJECT \\*\\/", "var tvEvents = " + tvEvents);
        }

        return new ResponseEntity(javascript, HttpStatus.OK);
    }
}
