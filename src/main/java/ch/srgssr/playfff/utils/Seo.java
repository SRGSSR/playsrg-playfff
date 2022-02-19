package ch.srgssr.playfff.utils;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
public class Seo {
    /*
        Convert a string to a seo name
        Based on Play SRG web application (from 'to-seo-string.ts')
     */
    public static String nameFromTitle(String title) {
        return title
                .toLowerCase()
                .replace(" - ", "-")
                .replace("–", "-")
                .replace(".", "-")

                // utf-8 non breaking space
                .replace("\\xa0", "-")

                // whitespace characters
                .replace("\\s", "-")
                .replace(" ", "-")

                // tabs and new line
                .replace("\t", "-")
                .replace("\n", "-")

                // umlauts", accents and diacritics
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replaceAll("[éèê]", "e")
                .replaceAll("[áàâ]", "a")
                .replaceAll("[úùû]", "u")
                .replaceAll("[óòô]", "o")
                .replace("ç", "c")
                .replace("æ", "ae")
                .replace("œ", "oe")

                // special characters (except "-"", "_")
                .replaceAll("[^a-zA-Z0-9\\-_]*", "");
    }
}
