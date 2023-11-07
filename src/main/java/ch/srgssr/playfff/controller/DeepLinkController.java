package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.DeepLinkJSContent;
import ch.srgssr.playfff.model.DeepLinkReport;
import ch.srgssr.playfff.service.DeepLinkService;
import ch.srgssr.playfff.service.DeepLinkReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.nio.charset.StandardCharsets;

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

    private MediaType javascriptMediaType = new MediaType("application", "javascript", StandardCharsets.UTF_8);

    @GetMapping(path = {"/api/v1/deeplink/report/{id}"})
    public ResponseEntity<DeepLinkReport> findOne(@PathVariable("id") int id) {
        return new ResponseEntity<>(deepLinkReportService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping(path = {"/api/v1/deeplink/report/{id}"})
    public ResponseEntity<DeepLinkReport> delete(@PathVariable("id") int id) {
        return new ResponseEntity<>(deepLinkReportService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/api/v1/deeplink/report")
    public ResponseEntity<Iterable<DeepLinkReport>> findAll() {
        return new ResponseEntity<>(deepLinkReportService.findAllByOrderByJsVersionDescCountDesc(), HttpStatus.OK);
    }

    // Public API
    @PostMapping("/api/v1/deeplink/report")
    public ResponseEntity<DeepLinkReport> create(@RequestBody DeepLinkReport deepLinkReport, WebRequest webRequest) {

        if (deepLinkReport == null
                || deepLinkReport.clientTime == null || deepLinkReport.clientId == null
                || deepLinkReport.jsVersion == 0 || deepLinkReport.url == null) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(deepLinkReportService.save(deepLinkReport), HttpStatus.CREATED);
        }
    }

    // Public API
    @RequestMapping(value={"/api/v{version}/deeplink/parsePlayUrl.js", "//api/v{version}/deeplink/parsePlayUrl.js"})
    @ResponseBody
    public ResponseEntity<String> parsePlayUrlJavascript(@PathVariable("version") int version) {

        DeepLinkJSContent deepLinkJSContent = service.getParsePlayUrlJSContent();

        String content = null;
        String hash = null;

        switch (version) {
            case 1:
               content = deepLinkJSContent.getContentV1();
               hash = deepLinkJSContent.getHashV1();
               break;
            case 2:
                content = deepLinkJSContent.getContentV2();
                hash = deepLinkJSContent.getHashV2();
                break;
        }

        if (content != null && hash != null) {
            return ResponseEntity.ok()
                    .contentType(javascriptMediaType)
                    .cacheControl(CacheControl.empty().cachePublic())
                    .eTag(hash)
                    .body(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
