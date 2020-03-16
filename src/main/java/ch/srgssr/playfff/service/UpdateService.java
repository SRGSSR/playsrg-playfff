package ch.srgssr.playfff.service;

import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.repository.UpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private Boolean updateCheckEnabled;

    public UpdateService(
            @Value("${UPDATE_CHECK_ENABLED:false}") String updateCheckEnabledString) {
        this.updateCheckEnabled = Boolean.valueOf(updateCheckEnabledString);
    }

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
        if (updateCheckEnabled) {
            List<Update> updates = repository.findByPackageNameAndVersion(packageName, version);

            if (updates.isEmpty()) {
                return null;
            } else {
                return updates.get(0);
            }
        }
        else {
            return null;
        }
    }

    public Update findById(long id) {
        return repository.findOne(id);
    }

    @Transactional
    public Update delete(long id) {
        Update update = findById(id);
        if (update != null) {
            repository.delete(update);
            return update;
        }
        return null;
    }

    public Iterable<Update> findAllDesc() {
        return repository.findAllByOrderByIdDesc();
    }
}
