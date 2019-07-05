package ch.srgssr.playfff.model;

import ch.srg.il.domain.v2_0.MediaType;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This very simple class represents a URN provided by the IL.
 */
public class IlUrn {
    private final static Pattern PATTERN_BUMAM = Pattern.compile("^urn:(srf|rts|rsi|rtr|swi|default):(?:[^:]+:)?(video|audio|videoset|show|assetgroup):([^:]+)$", Pattern.CASE_INSENSITIVE);
    private final static Pattern PATTERN_SWISSTXT = Pattern.compile("^urn:(swisstxt):(?:[^:]+:)?(video|audio|videoset|show|assetgroup):(srf|rts|rsi|rtr|swi):([^:]+)$", Pattern.CASE_INSENSITIVE);

    public static final String ASSET_VIDEO = "video";
    public static final String ASSET_VIDEO_SET = "videoset";
    public static final String ASSET_AUDIO = "audio";
    public static final String ASSET_SHOW = "show";
    public static final String ASSET_GROUP = "assetgroup";

    private String underlying;

    private String bu;
    private String assetType;
    private String id;
    private Mam mam;

    public enum Mam {
        SWISSTXT,
        RTS,
        RSI,
        RTR,
        SRF,
        SWI
    }

    /**
     * @param urn urn can be any media identifier, valid or not
     * @throws IllegalArgumentException if not a valid urn
     */
    public IlUrn(String urn) throws IllegalArgumentException {
        if (!parseBuMam(urn)
                && !parseSwissTxt(urn)) {
            throw new IllegalArgumentException(String.format("URN '%s' does not match a valid URN pattern.", urn));
        }
    }

    private boolean parseBuMam(String urn) {
        Matcher matcher;
        matcher = PATTERN_BUMAM.matcher(urn);
        if (matcher.matches()) {
            bu = matcher.group(1).toLowerCase(Locale.US);
            switch (bu) {
                case "rtr":
                    mam = Mam.RTR;
                    break;
                case "swi":
                    mam = Mam.SWI;
                    break;
                case "rts":
                    mam = Mam.RTS;
                    break;
                case "srf":
                    mam = Mam.SRF;
                    break;
                case "rsi":
                    mam = Mam.RSI;
                    break;
                default:
                    mam = null;
                    break;
            }
            assetType = matcher.group(2).toLowerCase(Locale.US);

            if (assetType.equals(ASSET_GROUP)) {
                // is a synonym of show (used in search request URN)
                assetType = ASSET_SHOW;
            }

            id = matcher.group(3); // Do not transform ID since it is case sensitive.
            underlying = "urn:" + bu + ":" + assetType + ":" + id;
            return true;
        } else {
            return false;
        }
    }

    private boolean parseSwissTxt(String urn) {
        Matcher matcher = PATTERN_SWISSTXT.matcher(urn);
        if (matcher.matches()) {
            String swisstxt = matcher.group(1).toLowerCase(Locale.US);
            bu = matcher.group(3).toLowerCase(Locale.US);
            mam = Mam.SWISSTXT;
            assetType = matcher.group(2).toLowerCase(Locale.US);
            if (assetType.equals(ASSET_GROUP)) {
                // is a synonym of show (used in search request URN)
                assetType = ASSET_SHOW;
            }
            id = matcher.group(4);
            underlying = "urn:" + swisstxt + ":" + assetType + ":" + bu + ":" + id;
            return true;
        } else {
            return false;
        }
    }

    public IlUrn(String bu, String assetType, String id) {
        this.bu = bu == null ? "default" : bu;
        this.assetType = assetType;
        this.id = id;
        underlying = "urn:" + this.bu + ":" + this.assetType + ":" + this.id;
    }

    public static String format(String bu, String assetType, String id) {
        return String.format("urn:%s:%s:%s", bu, assetType, id);
    }

    /**
     * Returns Business Unit.
     */
    public String getBu() {
        return bu;
    }

    /**
     * @return Swisstxt or BU.
     */
    public Mam getMam() {
        return mam;
    }

    /**
     * Return Asset Type (one of audio or video)
     */
    public String getAssetType() {
        return assetType;
    }

    public MediaType getMediaType() {
        if (isAudio()) {
            return MediaType.AUDIO;
        } else if (isVideo()) {
            return MediaType.VIDEO;
        } else {
            return null;
        }
    }

    /**
     * Return ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the underlying string representation.
     */
    @Override
    public String toString() {
        return underlying;
    }

    public boolean isAudio() {
        return ASSET_AUDIO.equals(assetType);
    }

    public boolean isVideo() {
        return ASSET_VIDEO.equals(assetType) || ASSET_VIDEO_SET.equals(assetType);
    }

    public boolean isShow() {
        return ASSET_SHOW.equals(assetType);
    }


    public boolean equalsToString(String o) {
        try {
            return underlying.equals(new IlUrn(o).toString());
        } catch (IllegalArgumentException invalidUrnException) {
            return false;
        }
    }
}
