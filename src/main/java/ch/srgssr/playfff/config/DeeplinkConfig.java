package ch.srgssr.playfff.config;

import ch.srgssr.playfff.service.DeeplinkService;
import ch.srgssr.playfff.service.ParsingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 *
 * License information is available from the LICENSE file.
 */
@Configuration
@EnableScheduling
public class DeeplinkConfig {

    @Autowired
    private DeeplinkService deeplinkService;

    @Autowired
    private ParsingReportService parsingReportService;

    @Scheduled(fixedDelayString = "${DEEPLINK_REFRESH_DELAY_MS:300000}")
    public void DeeplinkRefresh() {
        deeplinkService.refreshParsePlayUrlContent();
        parsingReportService.purgeOlderReports();
    }
}
