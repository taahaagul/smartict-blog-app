package com.taahaagul.smartictblog.response;

import com.taahaagul.smartictblog.entity.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private Long postId;
    private String text;
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public PostResponse(Post entity) {
        this.postId = entity.getId();
        this.text = entity.getText();
        this.userId = entity.getUser().getId();
        this.firstName = entity.getUser().getFirstName();
        this.lastName = entity.getUser().getLastName();
        this.userName = entity.getUser().getUsername();
        this.createdAt = entity.getCreatedAt();
        this.createdBy = entity.getCreatedBy();
        this.updatedAt = entity.getUpdatedAt();
        this.updatedBy = entity.getUpdatedBy();
    }
}