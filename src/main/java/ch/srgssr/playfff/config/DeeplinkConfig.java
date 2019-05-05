package ch.srgssr.playfff.config;

import ch.srgssr.playfff.service.DeeplinkService;
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
    private DeeplinkService service;

    @Scheduled(fixedDelay = 5 * 60000)
    public void IaaSStatusRefresh() {
        service.refreshParsePlayUrlContent();
    }
}
