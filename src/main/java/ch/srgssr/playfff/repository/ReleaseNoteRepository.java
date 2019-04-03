package ch.srgssr.playfff.repository;

import ch.srgssr.playfff.model.ReleaseNote;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public interface ReleaseNoteRepository extends CrudRepository<ReleaseNote, Long> {
    List<ReleaseNote> removeByPackageNameAndVersion(String packageName, String version);

    List<ReleaseNote> findByPackageNameAndVersion(String packageName, String version);
}
