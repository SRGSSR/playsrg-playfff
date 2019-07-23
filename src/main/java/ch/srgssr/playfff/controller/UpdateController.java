package ch.srgssr.playfff.controller;

import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.model.UpdateResult;
import ch.srgssr.playfff.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class UpdateController {
    @Autowired
    UpdateService updateService;

    @GetMapping(path = {"/api/v1/update/{id}"})
    public ResponseEntity<Update> findOne(@PathVariable("id") int id) {
        return new ResponseEntity<>(updateService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping(path = {"/api/v1/update/{id}"})
    public ResponseEntity<Update> delete(@PathVariable("id") int id) {
        return new ResponseEntity<>(updateService.delete(id), HttpStatus.OK);
    }

    @GetMapping("/api/v1/update")
    public ResponseEntity<Iterable<Update>> findAllDesc() {
        return new ResponseEntity<>(updateService.findAllDesc(), HttpStatus.OK);
    }

    @PostMapping("/api/v1/update")
    public ResponseEntity<Update> create(@RequestBody Update update) {
        if (update == null
                || update.packageName == null || update.packageName.length() == 0
                || update.version == null || update.version.length() == 0
                || update.text == null || update.text.length() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            return new ResponseEntity<>(updateService.save(update), HttpStatus.CREATED);
        }
    }

    // Public API
    @RequestMapping("/api/v1/update/check")
    public ResponseEntity<UpdateResult> updateText(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        Update update = updateService.getUpdate(packageName, version);
        return new ResponseEntity<>(new UpdateResult(update), HttpStatus.OK);
    }
}
