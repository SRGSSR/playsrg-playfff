package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeeplinkContent;
import ch.srgssr.playfff.model.ParsingReport;
import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.service.DeeplinkService;
import ch.srgssr.playfff.service.ParsingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
@CrossOrigin(origins = "*")
public class DeeplinkController {

    @Autowired
    private DeeplinkService service;

    @Autowired
    private ParsingReportService parsingReportService;

    @RequestMapping(value="/api/v1/deeplink/parse_play_url.js")
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

    @PostMapping("/api/v1/deeplink/report")
    public ResponseEntity<ParsingReport> create(@RequestBody ParsingReport parsingReport, WebRequest webRequest) {

        if (parsingReport == null
                || parsingReport.clientTime == null || parsingReport.clientId == null
                || parsingReport.jsVersion == null || parsingReport.url == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(parsingReportService.save(parsingReport), HttpStatus.CREATED);
        }
    }
}
