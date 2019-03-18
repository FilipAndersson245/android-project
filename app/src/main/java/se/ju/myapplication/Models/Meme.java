package se.ju.myapplication.Models;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;


public class Meme implements Comparable<Meme> {
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

    public int getHotness() {
        double order = Math.log10(Math.max(Math.abs(this.votes), 1));
        double sign = this.votes == 0 ? 0 : (this.votes > 0 ? 1 : -1);
        double seconds = (postDate.getTime() / 1000) - SCHPOOP_EPOCH;
        return (int) Math.round(7 * ((sign * order) + (seconds / 45000)));
    }

    @Override
    public int compareTo(Meme other) {
        return other.getHotness() - this.getHotness();
    }
}
