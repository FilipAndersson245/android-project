package se.ju.myapplication;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Meme {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("templateId")
    private Integer templateId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("name")
    private String name;
    @JsonProperty("imageSource")
    private String imageSource;
    @JsonProperty("votes")
    private Integer votes;
    @JsonProperty("postDate")
    private Timestamp postDate;
    private Integer vote = null;

    public Meme(){};

    public Meme(@NonNull Integer id,
                @NonNull Integer templateId,
                @NonNull String username,
                String name,
                @NonNull String imageSource,
                @NonNull Integer votes,
                @NonNull Timestamp postDate) {
        this.id = id;
        this.templateId = templateId;
        this.username = username;
        this.name = name;
        this.imageSource = imageSource;
        this.votes = votes;
        this.postDate = postDate;
    }

    public Timestamp getPostDate() {
        return postDate;
    }

    public Integer getVotes() {
        return votes;
    }

    public String getImageSource() {
        return imageSource;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public Integer getId() {
        return id;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public Integer getVote() {
        return vote;
    }
}
