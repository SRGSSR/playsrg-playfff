package ch.srgssr.playfff.repository;

import ch.srgssr.playfff.model.DeepLinkReport;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public interface DeepLinkReportRepository extends CrudRepository<DeepLinkReport, Long> {
    DeepLinkReport findFirstByClientIdAndJsVersionAndUrl(String clientId, int jsVersion, String url);

    List<DeepLinkReport> findAllByOrderByClientTimeDesc();

    List<DeepLinkReport> findAllByClientTimeLessThan(Date date);

    List<DeepLinkReport> findAllByOrderByJsVersionDescCountDesc();
}
