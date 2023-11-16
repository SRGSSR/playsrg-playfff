package ch.srgssr.playfff.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public enum Environment {
    PROD("il.srgssr.ch", "production"),
    STAGE("il-stage.srgssr.ch", "stage"),
    TEST("il-test.srgssr.ch", "test"),
    MMF("play-mmf.herokuapp.com", "play mmf");

    Environment(String url, String prettyName) {
        this.name = name().toLowerCase();
        this.baseUrl = url;
        this.prettyName = prettyName;
    }

    private final String name;
    private final String baseUrl;
    private final String prettyName;

    public static Environment fromValue(String v) {
        return valueOf(v.toUpperCase());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPrettyName() {
        return prettyName;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
