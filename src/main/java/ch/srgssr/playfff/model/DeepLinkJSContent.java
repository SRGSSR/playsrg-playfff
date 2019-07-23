package ch.srgssr.playfff.model;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class DeepLinkJSContent {

    private String content;
    private String hash;

    public DeepLinkJSContent(String content, String hash) {
        this.content = content;
        this.hash = hash;
    }

    public String getContent() {
        return content;
    }

    public String getHash() {
        return hash;
    }
}
