package com.example.pfff.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public enum Environment {
    PROD("il.srgssr.ch"),
    STAGE("il-stage.srgssr.ch"),
    TEST("il-test.srgssr.ch"),
    MMF("play-mmf.herokuapp.com");

    Environment(String url) {
        this.name = name().toLowerCase();
        this.baseUrl = url;
    }

    private String name;
    private String baseUrl;

    public static Environment fromValue(String v) {
        return valueOf(v.toUpperCase());
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
