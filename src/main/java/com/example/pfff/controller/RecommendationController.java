package com.example.pfff.controller;

import com.example.pfff.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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

    @RequestMapping("/api/v1/playlist/recommendation/{purpose}/{urn}")
    @ResponseBody
    Object recommendation(
            @PathVariable("purpose") String purpose,
            @PathVariable("urn") String urn,
            @RequestParam(value = "standalone", required = false, defaultValue = "false") Boolean standalone,
            @RequestParam(value = "format", required = false, defaultValue = "media") String format) {
        List<String> urns = service.getRecommendedUrns(purpose, urn, standalone);
        urns.add(0, urn);
        if ("urn".equals(format)) {
            return urns;
        } else { //if ("media".equals(format)) {
            return new ModelAndView(new RedirectView("http://il.srgssr.ch/integrationlayer/2.0/mediaList/byUrns.json?urns=" + String.join(",", urns)));
        }
    }
}
