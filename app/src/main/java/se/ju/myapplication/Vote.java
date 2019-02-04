package se.ju.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote
{
    @JsonProperty("memeId")
    private Integer memeId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("vote")
    private String vote;
}