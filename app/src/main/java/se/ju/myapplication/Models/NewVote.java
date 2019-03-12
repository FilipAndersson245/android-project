package se.ju.myapplication.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewVote
{
    public NewVote(String username, Integer vote) {
        this.username = username;
        this.vote = vote;
    }

    @JsonProperty("username")
    private String username;
    @JsonProperty("vote")
    private Integer vote;
}