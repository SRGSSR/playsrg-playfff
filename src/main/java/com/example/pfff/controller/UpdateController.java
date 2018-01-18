package com.example.pfff.controller;

import com.example.pfff.model.Update;
import com.example.pfff.model.UpdateResult;
import com.example.pfff.model.WhatIsNewResult;
import com.example.pfff.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/update/admin")
    public String updateAdmin(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "update/entry";
    }

    @RequestMapping("/update/save")
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
    @ResponseBody
    public UpdateResult updateText(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        return new UpdateResult(updateService.getUpdate(packageName, version));
    }
}
