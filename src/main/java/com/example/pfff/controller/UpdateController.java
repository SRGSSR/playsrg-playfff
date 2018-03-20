package com.example.pfff.controller;

import com.example.pfff.model.Update;
import com.example.pfff.model.UpdateResult;
import com.example.pfff.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Controller
public class UpdateController {
    @Autowired
    UpdateService updateService;

    @RequestMapping(value = "/update/admin", method = RequestMethod.GET)
    public String updateAdmin(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "update/entry";
    }

    @RequestMapping(value = "/update/admin", method = RequestMethod.POST)
    @ResponseBody
    public String updateSave(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version, @RequestParam(value = "text") String text, @RequestParam(value = "mandatory", required = false) boolean mandatory) {
        Update note = new Update();
        note.packageName = packageName;
        note.text = text;
        note.version = version;
        note.mandatory = mandatory;
        updateService.save(note);
        return "pushed";
    }

    @RequestMapping("/update/check")
    public ResponseEntity<UpdateResult> updateText(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        Update update = updateService.getUpdate(packageName, version);
        return new ResponseEntity<>(new UpdateResult(update), HttpStatus.OK);
    }
}
