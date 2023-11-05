package ch.srgssr.playfff.service;

import ch.srg.il.domain.v2_0.Topic;
import ch.srg.il.domain.v2_0.TopicList;
import ch.srgssr.playfff.config.CachingConfig;
import ch.srgssr.playfff.helper.BaseResourceString;
import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.model.Environment;
import ch.srgssr.playfff.utils.Seo;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @Cacheable(CachingConfig.DeepLinkCacheName)
    public DeepLinkJSContent getParsePlayUrlJSContent() {
        return refreshParsePlayUrlJSContent();
    }

    @CachePut(CachingConfig.DeepLinkCacheName)
    public synchronized DeepLinkJSContent refreshParsePlayUrlJSContent() {
        String javascriptV1 = BaseResourceString.getString(applicationContext, "deeplink/v1/parsePlayUrl.js");
        String javascriptV2 = BaseResourceString.getString(applicationContext, "deeplink/v2/parsePlayUrl.js");

        List<String> srgBUs = Arrays.asList("rsi", "rtr", "rts", "srf", "swi");

        Map<Environment, List<String>> busMap = new HashMap<>();
        busMap.put(Environment.PROD, srgBUs);
        busMap.put(Environment.STAGE, srgBUs);
        busMap.put(Environment.TEST, srgBUs);
        busMap.put(Environment.MMF, Arrays.asList());

        Map<String, Map<String, Map<String, String>>> tvGlobalTopicMap = new HashMap<>();

        for (Environment environment : pullEnvironmentSet) {
            Map<String, Map<String, String>> tvTopicMap = getTvTopicMap(busMap.get(environment), environment);

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

    private Map<String, Map<String, String>> getTvTopicMap(List<String> buList, Environment environment) {
        Map<String, Map<String, String>> tvTopicsMap = new HashMap<>();

        for (String bu : buList) {
            TopicList topicList = integrationLayerRequest.getTopics(bu, environment);
            if (topicList != null) {
                Map<String, String> tvTopicsSubMap = new HashMap<>();

                for (int i = 0; i < topicList.getList().size(); i++) {
                    Topic topic = topicList.getList().get(i);
                    tvTopicsSubMap.put(Seo.nameFromTitle(topic.getTitle()), topic.getId());
                }

                tvTopicsMap.put(bu, tvTopicsSubMap);
            }
        }
        return tvTopicsMap;
    }
}
