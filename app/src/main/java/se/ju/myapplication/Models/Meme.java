package se.ju.myapplication.Models;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;


public class Meme {
    public static final int SCHPOOP_EPOCH = 1543622400;

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
    @JsonProperty("hotness")
    private Integer hotness;
    private Integer vote = 0;

    public Meme() {
    }

    ;

    public Meme(@NonNull Integer id,
                Integer templateId,
                String username,
                String name,
                String imageSource,
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

    public void changeVotes(Integer newVote) {
        this.votes += newVote;
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
