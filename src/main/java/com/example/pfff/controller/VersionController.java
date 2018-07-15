package com.example.pfff.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class Version {
    public static final String VERSION = "Pfff...";

    @RequestMapping("/api/v1/version")
    @ResponseBody
    String home() {
        return VERSION;
    }
}
