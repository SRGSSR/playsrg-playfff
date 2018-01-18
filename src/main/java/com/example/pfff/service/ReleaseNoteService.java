package com.example.pfff.service;

import com.example.pfff.model.ReleaseNote;
import com.example.pfff.repository.ReleaseNoteRepository;
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
public class ReleaseNoteService {
    @Autowired
    private ReleaseNoteRepository repository;

    @Transactional
    public ReleaseNote save(ReleaseNote releaseNote) {
        repository.removeByPackageNameAndVersion(releaseNote.packageName, releaseNote.version);
        return repository.save(releaseNote);
    }

    public String getDisplayableText(String packageName, String version) {
        List<ReleaseNote> releaseNotes = repository.findByPackageNameAndVersion(packageName, version);

        if (releaseNotes.isEmpty()) {
            return null;
        } else {
            return releaseNotes.get(0).text;
        }
    }

    public String getDisplayableHtml(String packageName, String version) {
        List<ReleaseNote> releaseNotes = repository.findByPackageNameAndVersion(packageName, version);

        if (releaseNotes.isEmpty()) {
            return null;
        } else {
            return "<html><body><pre>" + releaseNotes.get(0).text + "\n</pre></body></html>";
        }
    }
}
