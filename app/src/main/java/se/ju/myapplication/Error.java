package se.ju.myapplication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error
{
    @JsonProperty("error")
    private String error;

    public String getError() {
        return error;
    }
}