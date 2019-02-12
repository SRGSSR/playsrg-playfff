package com.example.pfff.controller;

import com.example.pfff.model.RecommendedList;
import com.example.pfff.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class RecommendationController {
    @Autowired
    RecommendationService service;

    @Deprecated
    @RequestMapping("/api/v1/playlist/recommendation/{purpose}/{urn}")
    @ResponseBody
    Object recommendationV1(
            HttpServletRequest request,
            @PathVariable("purpose") String purpose,
            @PathVariable("urn") String urn,
            @RequestParam(value = "standalone", required = false, defaultValue = "false") boolean standalone,
            @RequestParam(value = "format", required = false, defaultValue = "media") String format) {
        RecommendedList recommendedList = getRecommendationList(purpose, urn, standalone);
        if ("urn".equals(format)) {
            return recommendedList.getUrns();
        } else { //if ("media".equals(format)) {
            UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
            builder.scheme(request.getScheme());
            builder.host("il.srgssr.ch");
            builder.path("integrationlayer/2.0/mediaList/byUrns.json");
            builder.queryParam("urns", String.join(",", recommendedList.getUrns()));
            return new ModelAndView(new RedirectView(builder.toUriString()));
        }
    }

    @RequestMapping("/api/v2/playlist/recommendation/{purpose}/{urn}")
    @ResponseBody
    RecommendedList recommendationV2(
            HttpServletRequest request,
            @PathVariable("purpose") String purpose,
            @PathVariable("urn") String urn,
            @RequestParam(value = "standalone", required = false, defaultValue = "false") boolean standalone) {
        return getRecommendationList(purpose, urn, standalone);
    }

    private RecommendedList getRecommendationList(@PathVariable("purpose") String purpose, @PathVariable("urn") String urn, @RequestParam(value = "standalone", required = false, defaultValue = "false") boolean standalone) {
        RecommendedList recommendedList = service.getRecommendedUrns(purpose, urn, standalone);
        recommendedList.addUrn(0, urn);
        return recommendedList;
    }


    @RequestMapping("/api/v2/playlist/personalRecommendation/{userId}")
    @ResponseBody
    RecommendedList personalRecommendation(
            HttpServletRequest request, @PathVariable("userId") String userId) {
        return service.getPersonalRecommendation("rtsPeachHome", userId);
    }

}
