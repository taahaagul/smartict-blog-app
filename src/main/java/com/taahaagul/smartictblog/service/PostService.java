package com.taahaagul.smartictblog.service;

import com.taahaagul.smartictblog.entity.Post;
import com.taahaagul.smartictblog.entity.User;
import com.taahaagul.smartictblog.exception.IncorrectValueException;
import com.taahaagul.smartictblog.exception.ResourceNotFoundException;
import com.taahaagul.smartictblog.repository.PostRepository;
import com.taahaagul.smartictblog.request.PostRequest;
import com.taahaagul.smartictblog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthenticationService authenticationService;
    private final PostRepository postRepository;

    /**
     * Get all posts with pagination
     * @param pageable
     * @return Page<PostResponse>
     */
    public Page<PostResponse> getAllPost(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(PostResponse::new);
    }

    /**
     * Get post by id
     * @param postId
     * @return PostResponse
     */
    public PostResponse getPostById(Long postId) {
        Post foundedPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        return new PostResponse(foundedPost);
    }

    /**
     * Get all posts of user with pagination
     * @param userId
     * @param pageable
     * @return Page<PostResponse>
     */
    public Page<PostResponse> getUserPost(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserId(userId, pageable);

        return posts.map(PostResponse::new);
    }

    /**
     * Create post for current user
     * @param postRequest
     * @return PostResponse
     */
    public PostResponse createPost(PostRequest postRequest) {
        User currentUser = authenticationService.getCurrentUser();
        Post post = Post.builder()
                .text(postRequest.getText())
                .user(currentUser)
                .build();

        Post savedPost = postRepository.save(post);
        return new PostResponse(savedPost);
    }

    /**
     * Delete post by id
     * @param postId
     */
    public void deletePost(Long postId) {
        Post foundedPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        postRepository.delete(foundedPost);
    }

    /**
     * Update post by id
     * @param postId
     * @param postRequest
     * @return PostResponse
     */
    public PostResponse updatePost(Long postId, PostRequest postRequest) {
        Post foundedPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));

        foundedPost.setText(postRequest.getText());
        Post savedPost = postRepository.save(foundedPost);
        return new PostResponse(savedPost);
    }

    /**
     * Check post owner
     * @param postId
     * @return boolean
     */
    public boolean isPostOwner(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId.toString()));
        User currentUser = authenticationService.getCurrentUser();
        return post.getUser().getId().equals(currentUser.getId());
    }
}