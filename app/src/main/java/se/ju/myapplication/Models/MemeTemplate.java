package se.ju.myapplication.Models;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemeTemplate
{
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("imageSource")
    private String imageSource;
    @JsonProperty("name")
    private String name;

    public MemeTemplate(){};

    public MemeTemplate(@NonNull Integer id, String name, @NonNull String imageSource, String username) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.username = username;
    }

    public Integer getId() { return this.id; }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSource() { return this.imageSource; }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}