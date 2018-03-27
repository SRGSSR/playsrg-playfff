package com.example.pfff.model;

import javax.persistence.*;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@Entity
@Table(name = "updates")
public class Update {
    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String packageName;
    public String version;

    @Column(columnDefinition = "text")
    public String text;
    public boolean mandatory;
}
