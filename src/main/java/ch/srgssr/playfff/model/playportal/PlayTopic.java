
package ch.srgssr.playfff.model.playportal;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "title",
    "urlEncodedTitle",
    "url",
    "latestModuleUrl",
    "mostClickedModuleUrl"
})
public class PlayTopic {

    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("urlEncodedTitle")
    private String urlEncodedTitle;
    @JsonProperty("url")
    private String url;
    @JsonProperty("latestModuleUrl")
    private String latestModuleUrl;
    @JsonProperty("mostClickedModuleUrl")
    private String mostClickedModuleUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("urlEncodedTitle")
    public String getUrlEncodedTitle() {
        return urlEncodedTitle;
    }

    @JsonProperty("urlEncodedTitle")
    public void setUrlEncodedTitle(String urlEncodedTitle) {
        this.urlEncodedTitle = urlEncodedTitle;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("latestModuleUrl")
    public String getLatestModuleUrl() {
        return latestModuleUrl;
    }

    @JsonProperty("latestModuleUrl")
    public void setLatestModuleUrl(String latestModuleUrl) {
        this.latestModuleUrl = latestModuleUrl;
    }

    @JsonProperty("mostClickedModuleUrl")
    public String getMostClickedModuleUrl() {
        return mostClickedModuleUrl;
    }

    @JsonProperty("mostClickedModuleUrl")
    public void setMostClickedModuleUrl(String mostClickedModuleUrl) {
        this.mostClickedModuleUrl = mostClickedModuleUrl;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
