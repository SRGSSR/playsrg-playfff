package ch.srgssr.playfff.repository;

import ch.srgssr.playfff.model.ParsingReport;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public interface ParsingReportRepository extends CrudRepository<ParsingReport, Long> {
    ParsingReport findFirstByClientIdAndJsVersionAndUrl(String clientId, String jsVersion, String url);

    List<ParsingReport> findAllByOrderByClientTimeDesc();

    List<ParsingReport> findAllByClientTimeLessThan(Date date);
}
