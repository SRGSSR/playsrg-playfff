package ch.srgssr.playfff.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Entity
@Table(name = "deeplink_reports")
public class DeepLinkReport {
    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "en_US_POSIX", pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Europe/Zurich")
    public Date clientTime;
    public String clientId;

    public int jsVersion;

    @Column(length = 512)
    public String url;

    public int count;
}
