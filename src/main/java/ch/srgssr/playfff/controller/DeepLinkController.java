package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.model.DeepLinkReport;
import ch.srgssr.playfff.service.DeepLinkService;
import ch.srgssr.playfff.service.DeepLinkReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
@CrossOrigin(origins = "*")
public class DeepLinkController {

    @Autowired
    private DeepLinkService service;

    @Autowired
    private DeepLinkReportService deepLinkReportService;

    @GetMapping(path = {"/api/v1/deeplink/report/{id}"})
    public ResponseEntity<DeepLinkReport> findOne(@PathVariable("id") int id) {
        return new ResponseEntity<>(deepLinkReportService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping(path = {"/api/v1/deeplink/report/{id}"})
    public ResponseEntity<DeepLinkReport> delete(@PathVariable("id") int id) {
        return new ResponseEntity<>(deepLinkReportService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/api/v1/deeplink/report")
    public ResponseEntity<Iterable<DeepLinkReport>> findAllByOrderByCountDesc() {
        return new ResponseEntity<>(deepLinkReportService.findAllByOrderByCountDesc(), HttpStatus.OK);
    }

    // Public API
    @PostMapping("/api/v1/deeplink/report")
    public ResponseEntity<DeepLinkReport> create(@RequestBody DeepLinkReport deepLinkReport, WebRequest webRequest) {

        if (deepLinkReport == null
                || deepLinkReport.clientTime == null || deepLinkReport.clientId == null
                || deepLinkReport.jsVersion == null || deepLinkReport.url == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(deepLinkReportService.save(deepLinkReport), HttpStatus.CREATED);
        }
    }

    // Public API
    @RequestMapping(value="/api/v1/deeplink/parse_play_url.js")
    @ResponseBody
    public ResponseEntity<String> parsePlayUrlJavascript() {

        DeepLinkJSContent deepLinkJSContent = service.getParsePlayUrlJSContent();

        if (deepLinkJSContent != null) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.empty().cachePublic())
                    .eTag(deepLinkJSContent.getHash())
                    .body(deepLinkJSContent.getContent());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
