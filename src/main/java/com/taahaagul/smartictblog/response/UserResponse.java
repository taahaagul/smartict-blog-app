package com.taahaagul.smartictblog.response;

import com.taahaagul.smartictblog.entity.Role;
import com.taahaagul.smartictblog.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private boolean enabled;
    private Role role;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public UserResponse(User entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.userName = entity.getUserName();
        this.email = entity.getEmail();
        this.enabled = entity.isEnabled();
        this.role = entity.getRole();
        this.createdAt = entity.getCreatedAt();
        this.createdBy = entity.getCreatedBy();
        this.updatedAt = entity.getUpdatedAt();
        this.updatedBy = entity.getUpdatedBy();
    }
}
