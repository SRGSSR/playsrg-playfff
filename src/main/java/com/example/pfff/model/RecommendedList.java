package com.example.pfff.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) SRG SSR. All rights reserved.
 * <p>
 * License information is available from the LICENSE file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendedList {

    private String recommendationId;
    private List<String> urns;

    public RecommendedList(String recommendationId, List<String> urns) {
        this.recommendationId = recommendationId;
        this.urns = urns;
    }

    public RecommendedList() {
        this.urns = new ArrayList<String>();
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public List<String> getUrns() {
        return urns;
    }

    public void addUrn(int index, String urn) {
        this.urns.add(index, urn);
    }
}