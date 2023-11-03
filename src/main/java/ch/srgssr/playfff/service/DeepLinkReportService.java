package ch.srgssr.playfff.service;

import ch.srgssr.playfff.model.DeepLinkReport;
import ch.srgssr.playfff.repository.DeepLinkReportRepository;
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
public class DeepLinkReportService {
    @Autowired
    private DeepLinkReportRepository repository;

    private int maxDeepLinkReports;

    public DeepLinkReportService(
            @Value("${MAX_DEEP_LINK_REPORTS:2500}") String maxDeepLinkReportsString) {

        this.maxDeepLinkReports = Integer.valueOf(maxDeepLinkReportsString);
    }

    @Transactional
    public DeepLinkReport save(DeepLinkReport deepLinkReport) {
        DeepLinkReport currentDeepLinkReport = getParsingReport(deepLinkReport.clientId, deepLinkReport.jsVersion, deepLinkReport.url);
        if (currentDeepLinkReport != null) {
            currentDeepLinkReport.count += 1;
            currentDeepLinkReport.clientTime = deepLinkReport.clientTime;
            return repository.save(currentDeepLinkReport);
        } else {
            deepLinkReport.count = 1;
            return repository.save(deepLinkReport);
        }
    }

    @Transactional
    public synchronized void purgeOlderReports() {
        // Keep only latest reports
        if (repository.count() > maxDeepLinkReports) {
            List<DeepLinkReport> allReports = repository.findAllByOrderByClientTimeDesc();
            DeepLinkReport report = allReports.get(maxDeepLinkReports - 1);
            List<DeepLinkReport> olderReports = repository.findAllByClientTimeLessThan(report.clientTime);
            repository.deleteAll(olderReports);
        }
    }

    private DeepLinkReport getParsingReport(String clientId, int jsVersion, String url) {
        return repository.findFirstByClientIdAndJsVersionAndUrl(clientId, jsVersion, url);
    }

    public DeepLinkReport findById(long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public DeepLinkReport delete(long id) {
        DeepLinkReport deepLinkReport = findById(id);
        if (deepLinkReport != null) {
            repository.delete(deepLinkReport);
            return deepLinkReport;
        }
        return null;
    }

    public Iterable<DeepLinkReport> findAllByOrderByJsVersionDescCountDesc() {
        return repository.findAllByOrderByJsVersionDescCountDesc();
    }
}
