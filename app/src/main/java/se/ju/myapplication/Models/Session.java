package se.ju.myapplication.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session
{
    @JsonProperty("grant_type")
    private String grant_type;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public Session(String grant_type, String username, String password) {
        this.grant_type = grant_type;
        this.username = username;
        this.password = password;
    }
}