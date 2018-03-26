package com.example.pfff.controller;

import com.example.pfff.model.Update;
import com.example.pfff.model.UpdateResult;
import com.example.pfff.service.UpdateService;
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

    @RequestMapping(value = "/update/remove", method = RequestMethod.POST)
    @ResponseBody
    public String updateRemove(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        updateService.remove(packageName, version);
        return "removed";
    }

    @RequestMapping("/api/v1/update/check")
    public ResponseEntity<UpdateResult> updateText(@RequestParam(value = "package") String packageName, @RequestParam(value = "version") String version) {
        Update update = updateService.getUpdate(packageName, version);
        return new ResponseEntity<>(new UpdateResult(update), HttpStatus.OK);
    }


    @PostMapping("/api/v1/update")
    public Update create(@RequestBody Update update){
        return updateService.create(update);
    }

    @GetMapping(path = {"/api/v1/update/{id}"})
    public Update findOne(@PathVariable("id") int id){
        return updateService.findById(id);
    }

    @PutMapping("/api/v1/update")
    public Update update(@RequestBody Update update){
        return updateService.update(update);
    }

    @DeleteMapping(path ={"/api/v1/update/{id}"})
    public Update delete(@PathVariable("id") int id) {
        return updateService.delete(id);
    }

    @GetMapping("/api/v1/update")
    public Iterable<Update> findAll(){
        return updateService.findAll();
    }
}
