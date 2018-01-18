package com.example.pfff.repository;

import com.example.pfff.model.ReleaseNote;
import com.example.pfff.model.Update;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public interface UpdateRepository extends CrudRepository<Update, Long> {
    List<Update> removeByPackageNameAndVersion(String packageName, String version);

    List<Update> findByPackageNameAndVersion(String packageName, String version);
}
