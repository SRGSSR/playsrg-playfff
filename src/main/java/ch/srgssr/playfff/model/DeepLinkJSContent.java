package ch.srgssr.playfff.model;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class DeepLinkJSContent {

    private final String contentV1;
    private final String hashV1;

    private final String contentV2;
    private final String hashV2;

    public DeepLinkJSContent(String contentV1, String hashV1, String contentV2, String hashV2) {
        this.contentV1 = contentV1;
        this.hashV1 = hashV1;
        this.contentV2 = contentV2;
        this.hashV2 = hashV2;
    }

    public String getContentV1() {
        return contentV1;
    }

    public String getHashV1() {
        return hashV1;
    }

    public String getContentV2() {
        return contentV2;
    }

    public String getHashV2() {
        return hashV2;
    }
}
