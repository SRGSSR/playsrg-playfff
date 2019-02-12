package com.example.pfff.model.peach;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalRecommendationResult {
    public String status;
    public String codops;
    public Result result;

    public static class Result {

        @JsonProperty("fallback_used")
        public boolean fallbackUsed;
        public String title;
        public List<Item> items;
        public String id;
    }
    public static class Item {

        public String urn;
        //TODO Implement once explanation semantics has been implemented in peach backend
        public Explanation explanation;

    }
    /**
     * Explanation has been defined in Jan. 2019 but not yet used.
     */
    public static class Explanation {

        public String text;
        @JsonProperty("media_reference_urn")
        public String mediaReferenceUrn;
    }

    public String getTitle() {
        return result != null ? result.title : null;
    }

    public String getRecommendationId() {
        return result == null ? null : result.id;
    }

    public List<String> getUrns() {
        if (result != null && result.items != null) {
            return result.items.stream().map((i) -> i.urn).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}