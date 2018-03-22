package com.example.pfff.controller;

import com.example.pfff.model.ReleaseNote;
import com.example.pfff.model.WhatIsNewResult;
import com.example.pfff.service.ReleaseNoteService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WhatIsNew {
    @Autowired
    ReleaseNoteService releaseNoteService;

    @RequestMapping(value = "/whatisnew/admin", method = RequestMethod.GET)
    public String whatisnewAdmin(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "whatisnew/entry";
    }

    @RequestMapping(value = "/whatisnew/admin", method = RequestMethod.POST)
    @ResponseBody
    public String whatisnewSave(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version, @RequestParam(value = "text") String text) {
        ReleaseNote note = new ReleaseNote();
        note.packageName = packageName;
        note.text = text;
        note.version = version;
        releaseNoteService.save(note);
        return "pushed";
    }

    @RequestMapping("/api/v1/whatisnew/text")
    @ResponseBody
    public WhatIsNewResult whatisnewText(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        return new WhatIsNewResult(releaseNoteService.getDisplayableText(packageName, version));
    }

    @RequestMapping("/api/v1/whatisnew/html")
    @ResponseBody
    public String whatisnewHtml(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        return releaseNoteService.getDisplayableHtml(packageName, version);
    }

}
