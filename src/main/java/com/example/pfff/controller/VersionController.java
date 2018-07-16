package com.example.pfff.controller;

import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class VersionController {

    @Value("${version}")
    private String version;

    @RequestMapping("/api/v1/version")
    @ResponseBody
    public ResponseEntity<String> version() {
        return new ResponseEntity<>(version, HttpStatus.OK);
    }

    @VisibleForTesting
    public String getVersion() {
        return version;
    }
}
