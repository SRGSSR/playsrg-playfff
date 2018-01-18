package com.example.pfff.model;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class WhatIsNewResult {
    /**
     * Text to be displayed, may contain unicode characters.
     */
    public String text;
    /**
     * Title. If null, client default string is displayed.
     */
    public String title;

    public WhatIsNewResult(String text) {
        this.text = text;
    }

}
