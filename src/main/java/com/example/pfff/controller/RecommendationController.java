package com.example.pfff.controller;

import com.example.pfff.model.RecommendedList;
import com.example.pfff.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class RecommendationController {
    @Autowired
    RecommendationService service;

    @RequestMapping("/api/v{version}/playlist/recommendation/{purpose}/{urn}")
    @ResponseBody
    Object recommendation(
            HttpServletRequest request,
            @PathVariable("version") Integer version,
            @PathVariable("purpose") String purpose,
            @PathVariable("urn") String urn,
            @RequestParam(value = "standalone", required = false, defaultValue = "false") boolean standalone,
            @RequestParam(value = "format", required = false, defaultValue = "media") String format) {
        RecommendedList recommendedList = service.getRecommendedUrns(purpose, urn, standalone);
        recommendedList.addUrn(0, urn);
        if ("urn".equals(format)) {
            if (version == 1) {
                return recommendedList.getUrns();
            }
            else {
                return recommendedList;
            }
        } else { //if ("media".equals(format)) {
            UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
            builder.scheme(request.getScheme());
            builder.host("il.srgssr.ch");
            builder.path("integrationlayer/2.0/mediaList/byUrns.json");
            builder.queryParam("urns", String.join(",", recommendedList.getUrns()));
            return new ModelAndView(new RedirectView(builder.toUriString()));
        }
    }
}
