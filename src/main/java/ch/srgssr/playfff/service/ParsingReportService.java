package ch.srgssr.playfff.service;

import ch.srgssr.playfff.model.ParsingReport;
import ch.srgssr.playfff.model.Update;
import ch.srgssr.playfff.repository.ParsingReportRepository;
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
public class ParsingReportService {
    @Autowired
    private ParsingReportRepository repository;

    private int maxParsingReports;

    public ParsingReportService(
            @Value("${MAX_PARSING_REPORTS:2500}") String maxParsingReportsString) {

        this.maxParsingReports = Integer.valueOf(maxParsingReportsString);
    }

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

    @Transactional
    public synchronized void purgeOlderReports() {
        // Keep only latest reports
        if (repository.count() > maxParsingReports) {
            List<ParsingReport> allReports = repository.findAllByOrderByClientTimeDesc();
            ParsingReport report = allReports.get(maxParsingReports - 1);
            List<ParsingReport> olderReports = repository.findAllByClientTimeLessThan(report.clientTime);
            repository.delete(olderReports);
        }
    }

    private ParsingReport getParsingReport(String clientId, String jsVersion, String url) {
        return repository.findFirstByClientIdAndJsVersionAndUrl(clientId, jsVersion, url);
    }
}
