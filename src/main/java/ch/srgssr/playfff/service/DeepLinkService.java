package ch.srgssr.playfff.service;

import ch.srg.il.domain.v2_0.ModuleConfig;
import ch.srg.il.domain.v2_0.ModuleConfigList;
import ch.srgssr.playfff.helper.BaseResourceString;
import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.model.Environment;
import ch.srgssr.playfff.model.playportal.PlayTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Service
public class DeepLinkService {
    private static final Logger logger = LoggerFactory.getLogger(DeepLinkService.class);

    private DeepLinkJSContent parsePlayUrlContent;

    private RestTemplate restTemplate;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    public DeepLinkService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    @Cacheable("DeeplinkParsePlayUrlJSContent")
    public DeepLinkJSContent getParsePlayUrlJSContent() {
        if (parsePlayUrlContent == null) {
            refreshParsePlayUrlJSContent();
        }

        return parsePlayUrlContent;
    }

    @CachePut("DeeplinkParsePlayUrlJSContent")
    public synchronized DeepLinkJSContent refreshParsePlayUrlJSContent() {
        String javascript = BaseResourceString.getString(applicationContext, "parsePlayUrl.js");

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
            URI tvTopicListUri = null;
            try {
                tvTopicListUri = new URI("https", null, bu.getValue(), 443, "/play/tv/topicList",
                        null, null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
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
            javascript = javascript.replaceAll("\\/\\* INJECT TVTOPICS OBJECT \\*\\/", "var tvTopics = " + tvTopics + ";");
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
            javascript = javascript.replaceAll("\\/\\* INJECT TVEVENTS OBJECT \\*\\/", "var tvEvents = " + tvEvents + ";");
        }

        String buildHash = sha1(javascript);
        Date buildDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
        String strDate = dateFormat.format(buildDate);
        javascript = javascript.replaceAll("var parsePlayUrlBuild = \"mmf\";", "var parsePlayUrlBuild = \"" + buildHash + "\";\nvar parsePlayUrlBuildDate = \"" + strDate + "\";");

        parsePlayUrlContent = new DeepLinkJSContent(javascript, buildHash);
        return parsePlayUrlContent;
    }

    private String sha1(String input) {
        String sha1 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes("UTF-8"), 0, input.length());
            sha1 = DatatypeConverter.printHexBinary(msdDigest.digest());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            logger.warn(e.getMessage());
        }
        return sha1;
    }
}
