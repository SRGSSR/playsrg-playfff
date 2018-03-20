package com.example.pfff.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.istack.internal.Nullable;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
