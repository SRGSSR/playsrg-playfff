package com.example.pfff.model.peach;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResult {
    @JsonProperty("status")
    private String status;

    @JsonProperty("result")
    private Result result;

    private static class Result {
        @JsonProperty("items")
        private Item[] items;
        @JsonProperty("id")
        private String id;
    }

    private static class Item {
        @JsonProperty("id")
        private String id;
        @JsonProperty("urn")
        private String urn;
    }

    public List<String> getUrns() {
        ArrayList<String> urns = new ArrayList<>();
        if (result != null) {
            if (result.items != null) {
                for (int i = 0; i < result.items.length; i++) {
                    Item item = result.items[i];
                    if (item.urn != null) {
                        urns.add(item.urn);
                    } else {
                        urns.add("urn:rts:video:" + item.id);
                    }
                }
                return urns;
            }
        }
        return urns;
    }

    public String getRecommendationId() {
        return result.id;
    }
}
