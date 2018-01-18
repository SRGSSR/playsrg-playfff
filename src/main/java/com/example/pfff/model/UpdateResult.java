package com.example.pfff.model;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class UpdateResult {
    /**
     * Text to be displayed, may contain unicode characters.
     */
    public String text;
    /**
     * Title. If null, client default string is displayed.
     */
    public String title;
    /**
     * Mandatory. Client must force update if true.
     */
    public boolean mandatory;

    public UpdateResult(Update update) {
        this.text = update.text;
        this.mandatory = update.mandatory;
    }

}
