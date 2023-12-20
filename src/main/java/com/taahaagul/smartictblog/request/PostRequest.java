package com.taahaagul.smartictblog.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PostRequest {

    @NotEmpty(message = "text is requiured")
    private String text;
}