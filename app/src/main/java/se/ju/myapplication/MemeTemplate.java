package se.ju.myapplication;

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

    public MemeTemplate(@NonNull Integer id, String name, @NonNull String imageSource, String username) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.username = username;
    }

//    public Meme(@NonNull Integer id,
//                @NonNull Integer templateId,
//                @NonNull String username,
//                String name,
//                @NonNull String imageSource,
//                @NonNull Integer votes,
//                @NonNull Timestamp postDate) {
//        this.id = id;
//        this.templateId = templateId;
//        this.username = username;
//        this.name = name;
//        this.imageSource = imageSource;
//        this.votes = votes;
//        this.postDate = postDate;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}