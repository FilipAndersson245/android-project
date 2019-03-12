package se.ju.myapplication.Models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewMeme
{
    @JsonProperty("templateId")
    private Integer templateId;
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

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public NewMeme(Integer templateId, String username, String imageSource) {
        this.templateId = templateId;
        this.username = username;
        this.imageSource = imageSource;
    }
}
