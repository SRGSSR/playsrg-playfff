package com.example.pfff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    enum Type {
        None,
        Optional,
        Mandatory
    }

    /**
     * Type of update.
     */
    public Type type;

    public UpdateResult(Update update) {
        if (update != null) {
            this.text = update.text;
            this.type = update.mandatory ? Type.Mandatory : Type.Optional;
        } else {
            type = Type.None;
        }
    }

}
