package com.taahaagul.smartictblog.controller;

import com.taahaagul.smartictblog.request.PostRequest;
import com.taahaagul.smartictblog.response.ErrorResponse;
import com.taahaagul.smartictblog.response.PostResponse;
import com.taahaagul.smartictblog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Post",
        description = "Post API endpoints"
)
@RestController
@RequestMapping(path = "/api/post", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Get All Posts Rest Api",
            description = "Rest API endpoint that returns all posts in a paginated way"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('post:read')")
    public ResponseEntity<Page<PostResponse>> getAllPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.getAllPost(pageable));
    }

    @Operation(
            summary = "Get Post By Id Rest Api",
            description = "Rest API endpoint that returns a post by id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:read')")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.getPostById(postId));
    }

    @Operation(
            summary = "Get User Posts Rest Api",
            description = "Rest API endpoint that returns all posts of a user in a paginated way"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('post:read')")
    public ResponseEntity<Page<PostResponse>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(postService.getUserPost(userId, pageable));
    }

    @Operation(
            summary = "Create Post Rest Api",
            description = "Rest API endpoint that creates a post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "HTTP Status Created"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('post:create')")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postResponse);
    }

    @Operation(
            summary = "Update Post Rest Api",
            description = "Rest API endpoint that updates a post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:update') or @postService.isPostOwner(#postId)")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.updatePost(postId, postRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(postResponse);
    }

    @Operation(
            summary = "Delete Post Rest Api",
            description = "Rest API endpoint that deletes a post"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "HTTP Status Not Found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:delete') or @postService.isPostOwner(#postId)")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Post deleted successfully");
    }
}