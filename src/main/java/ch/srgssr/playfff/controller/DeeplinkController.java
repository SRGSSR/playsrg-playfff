package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeeplinkContent;
import ch.srgssr.playfff.service.DeeplinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 *
 * License information is available from the LICENSE file.
 */
@Controller
@RequestMapping(value = "/deeplink")
public class DeeplinkController {

    @Autowired
    private DeeplinkService service;

    @RequestMapping(value="/v1/parse_play_url.js")
    @ResponseBody
    public ResponseEntity<String> parsePlayUrlJavascript() {

        DeeplinkContent deeplinkContent = service.getParsePlayUrlContent();

        if (deeplinkContent != null) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.empty().cachePublic())
                    .eTag(deeplinkContent.getHash())
                    .body(deeplinkContent.getContent());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
