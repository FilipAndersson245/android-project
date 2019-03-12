package se.ju.myapplication.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionResponse
{
    @JsonProperty("accessToken")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}