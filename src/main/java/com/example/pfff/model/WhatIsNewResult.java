package com.example.pfff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhatIsNewResult {
    /**
     * Text to be displayed, may contain unicode characters.
     */
    public String text;

    public WhatIsNewResult(String text) {
        this.text = text;
    }
}
