package se.ju.myapplication;

import android.support.annotation.NonNull;

import java.sql.Timestamp;

public class Meme {
    public static final String basePath = "";
    private final Integer id;
    private final Integer templateId;
    private final String username;
    private final String name;
    private final String imageSource;
    private final Integer votes;
    private final Timestamp postDate;

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
}
