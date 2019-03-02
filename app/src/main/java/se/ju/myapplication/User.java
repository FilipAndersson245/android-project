package se.ju.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}