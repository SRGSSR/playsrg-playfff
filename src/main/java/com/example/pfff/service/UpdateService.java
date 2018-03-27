package com.example.pfff.service;

import com.example.pfff.model.Update;
import com.example.pfff.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Repository
public class UpdateService {
    @Autowired
    private UpdateRepository repository;

    @Transactional
    public Update save(Update update) {
        repository.removeByPackageNameAndVersion(update.packageName, update.version);
        return repository.save(update);
    }

    @Transactional
    public void remove(String packageName, String version) {
        repository.removeByPackageNameAndVersion(packageName, version);
    }

    public Update getUpdate(String packageName, String version) {
        List<Update> updates = repository.findByPackageNameAndVersion(packageName, version);

        if (updates.isEmpty()) {
            return null;
        } else {
            return updates.get(0);
        }
    }

    public Update create(Update update) {
        return repository.save(update);
    }

    public Update findById(long id) {
        return repository.findOne(id);
    }

    public Update update(Update update) {
        return repository.save(update);
    }

    public Update delete(long id) {
        Update update = findById(id);
        if (update != null) {
             repository.delete(update);
            return update;
        }
        return null;
    }


    public Iterable<Update> findAll() {
        return repository.findAll();
    }
}
