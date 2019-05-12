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
    public ParsingReport save(ParsingReport parsingReport) {
        ParsingReport currentParsingReport = getParsingReport(parsingReport.clientId, parsingReport.jsVersion, parsingReport.url);
        if (currentParsingReport != null) {
            currentParsingReport.count += 1;
            currentParsingReport.clientTime = parsingReport.clientTime;
            return repository.save(currentParsingReport);
        } else {
            parsingReport.count = 1;
            return repository.save(parsingReport);
        }
    }

    private ParsingReport getParsingReport(String clientId, String jsVersion, String url) {
        List<ParsingReport> parsingReports = repository.findByClientIdAndJsVersionAndUrl(clientId, jsVersion, url);

        if (parsingReports.isEmpty()) {
            return null;
        } else {
            return parsingReports.get(0);
        }
    }
}
