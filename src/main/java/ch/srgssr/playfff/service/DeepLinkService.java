package ch.srgssr.playfff.service;

import ch.srgssr.playfff.helper.BaseResourceString;
import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.model.Environment;
import ch.srgssr.playfff.model.playportal.PlayTopic;
import ch.srgssr.playfff.utils.Sha1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Service
@EnableCaching
public class DeepLinkService {
    private static final Logger logger = LoggerFactory.getLogger(DeepLinkService.class);

    private static final String DeepLinkCacheName = "DeeplinkParsePlayUrlJSContent";

    private RestTemplate restTemplate;

    private Set<Environment> pullEnvironmentSet = new HashSet<>();

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private IntegrationLayerRequest integrationLayerRequest;

    public DeepLinkService(RestTemplateBuilder restTemplateBuilder,
                           @Value("${DEEP_LINK_ENVIRONMENTS:PROD}") String environments) {
        restTemplate = restTemplateBuilder.build();

        String[] environmentStrings = environments.split(",");
        for (String environmentString : environmentStrings) {
            pullEnvironmentSet.add(Environment.fromValue(environmentString));
        }
    }

    @Cacheable(DeepLinkCacheName)
    public DeepLinkJSContent getParsePlayUrlJSContent() {
        return refreshParsePlayUrlJSContent();
    }

    @CachePut(DeepLinkCacheName)
    public synchronized DeepLinkJSContent refreshParsePlayUrlJSContent() {
        String javascriptV1 = BaseResourceString.getString(applicationContext, "deeplink/v1/parsePlayUrl.js");
        String javascriptV2 = BaseResourceString.getString(applicationContext, "deeplink/v2/parsePlayUrl.js");

        Map<String, String> buProdMap = new HashMap<>();
        buProdMap.put("srf", "srgplayer-srf.production.srf.ch");
        buProdMap.put("rts", "srgplayer-rts.production.srf.ch");
        buProdMap.put("rsi", "srgplayer-rsi.production.srf.ch");
        buProdMap.put("rtr", "srgplayer-rtr.production.srf.ch");
        buProdMap.put("swi", "srgplayer-swi.production.srf.ch");

        Map<String, String> buStageMap = new HashMap<>();
        buStageMap.put("srf", "srgplayer-srf.stage.srf.ch");
        buStageMap.put("rts", "srgplayer-rts.stage.srf.ch");
        buStageMap.put("rsi", "srgplayer-rsi.stage.srf.ch");
        buStageMap.put("rtr", "srgplayer-rtr.stage.srf.ch");
        buStageMap.put("swi", "srgplayer-swi.stage.srf.ch");

        Map<String, String> buTestMap = new HashMap<>();
        buTestMap.put("srf", "srgplayer-srf.test.srf.ch");
        buTestMap.put("rts", "srgplayer-rts.test.srf.ch");
        buTestMap.put("rsi", "srgplayer-rsi.test.srf.ch");
        buTestMap.put("rtr", "srgplayer-rtr.test.srf.ch");
        buTestMap.put("swi", "srgplayer-swi.test.srf.ch");

        Map<Environment, Map<String, String>> environmentMap = new HashMap<>();
        environmentMap.put(Environment.PROD, buProdMap);
        environmentMap.put(Environment.STAGE, buStageMap);
        environmentMap.put(Environment.TEST, buTestMap);
        environmentMap.put(Environment.MMF, new HashMap<>());

        Map<String, Map<String, Map<String, String>>> tvGlobalTopicMap = new HashMap<>();

        for (Environment environment : pullEnvironmentSet) {
            Map<String, Map<String, String>> tvTopicMap = getTvTopicMap(environmentMap.get(environment));

            if (tvTopicMap.size() > 0) {
                tvGlobalTopicMap.put(environment.getPrettyName(), tvTopicMap);
            }
        }

        ObjectMapper mapperObj = new ObjectMapper();

        String tvTopics = null;
        try {
            tvTopics = mapperObj.writeValueAsString(tvGlobalTopicMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tvTopics != null) {
            javascriptV1 = javascriptV1.replaceAll("\\/\\* INJECT TVTOPICS OBJECT \\*\\/", "var tvTopics = " + tvTopics + ";");
            javascriptV2 = javascriptV2.replaceAll("\\/\\* INJECT TVTOPICS OBJECT \\*\\/", "var tvTopics = " + tvTopics + ";");
        }

        String buildHashV1 = "NO_SHA1";
        try {
            buildHashV1 = Sha1.sha1(javascriptV1);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.warn("sha1 v1", e);
        }
        String buildHashV2 = "NO_SHA1";
        try {
            buildHashV2 = Sha1.sha1(javascriptV2);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.warn("sha1 v2", e);
        }
        Date buildDate = new Date();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
        String strDate = dateFormat.format(buildDate);
        javascriptV1 = javascriptV1.replaceAll("var parsePlayUrlBuild = \"mmf\";", "var parsePlayUrlBuild = \"" + buildHashV1 + "\";\nvar parsePlayUrlBuildDate = \"" + strDate + "\";");
        javascriptV2 = javascriptV2.replaceAll("var parsePlayUrlBuild = \"mmf\";", "var parsePlayUrlBuild = \"" + buildHashV2 + "\";\nvar parsePlayUrlBuildDate = \"" + strDate + "\";");

        return new DeepLinkJSContent(javascriptV1, buildHashV1, javascriptV2, buildHashV2);
    }

    private Map<String, Map<String, String>> getTvTopicMap(Map<String, String> buMap) {
        Map<String, Map<String, String>> tvTopicsMap = new HashMap<>();

        for (Map.Entry<String, String> bu : buMap.entrySet()) {
            URI tvTopicListUri = null;
            try {
                tvTopicListUri = new URI("https", null, bu.getValue(), 443, "/play/tv/topicList",
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tvTopicsMap;
    }
}
