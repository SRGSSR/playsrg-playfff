package ch.srgssr.playfff.service;

import ch.srgssr.playfff.model.ParsingReport;
import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.repository.ParsingReportRepository;
import ch.srgssr.playfff.repository.UpdateRepository;
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
public class ParsingReportService {
    @Autowired
    private ParsingReportRepository repository;

    @Transactional
    public ParsingReport create(ParsingReport parsingReport) {
        return repository.save(parsingReport);
    }
}
