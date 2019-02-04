package se.ju.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Meme
{
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("templateId")
    private Integer templateId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("imageSource")
    private String imageSource;
    @JsonProperty("name")
    private String name;
    @JsonProperty("votes")
    private Integer votes;
    @JsonProperty("postDate")
    private String postDate;

    public String getUsername() {
        return username;
    }
}