package se.ju.myapplication.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewMemeTemplate
{
    @JsonProperty("username")
    private String username;
    @JsonProperty("name")
    private String name;
    @JsonProperty("imageSource")
    private String imageSource;
    @JsonProperty("topText")
    private String topText;
    @JsonProperty("bottomText")
    private String bottomText;

    public void setName(String name) {
        this.name = name;
    }

    public NewMemeTemplate(Integer templateId, String username, String imageSource) {
        this.username = username;
        this.imageSource = imageSource;
    }
}
