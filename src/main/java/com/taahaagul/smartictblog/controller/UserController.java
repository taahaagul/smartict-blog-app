package com.taahaagul.smartictblog.controller;

import com.taahaagul.smartictblog.request.UserChangePaswRequest;
import com.taahaagul.smartictblog.request.UserUpdateRequest;
import com.taahaagul.smartictblog.response.ErrorResponse;
import com.taahaagul.smartictblog.response.UserResponse;
import com.taahaagul.smartictblog.service.UserService;
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


import static org.springframework.http.HttpStatus.OK;

@Tag(
        name = "User",
        description = "User API endpoints"
)
@RestController
@RequestMapping(path = "/api/user", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get Current User Rest Api",
            description = "Rest API endpoint that returns current user"
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
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.status(OK)
                .body(userService.getCurrentUser());
    }

    @Operation(
            summary = "Change Password Rest Api",
            description = "Rest API endpoint that changes password of current user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "HTTP Status Accepted"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody UserChangePaswRequest request) {
        userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Change Password Successfully");
    }

    @Operation(
            summary = "Update User Rest Api",
            description = "Rest API endpoint that updates current user"
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
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.status(OK)
                .body(userService.updateUser(request));
    }

    @Operation(
            summary = "Get Any User Rest Api",
            description = "Rest API endpoint that returns any user"
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
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getAnyUser(@PathVariable Long userId) {
        return ResponseEntity.status(OK)
                .body(userService.getAnyUser(userId));
    }

    @Operation(
            summary = "Get All Users Rest Api",
            description = "Rest API endpoint that returns all users"
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
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.status(OK)
                .body(userService.getAllUsers(pageable));
    }

    @Operation(
            summary = "Update User Role Rest Api",
            description = "Rest API endpoint that updates user role"
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
    @PutMapping("/update-role/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('user:change-role')")
    public ResponseEntity<String> updateUserRole(
            @PathVariable Long userId,
            @PathVariable String userRole
    ) {
        userService.updateUserRole(userId, userRole);
        return ResponseEntity.status(OK)
                .body("User role changed succsessfully");
    }

    @Operation(
            summary = "Change User Enabled Status Rest Api",
            description = "Rest API endpoint that changes user enabled status"
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
    @PutMapping("/change-enabled/{userId}")
    @PreAuthorize("hasAuthority('user:change-enabled')")
    public ResponseEntity<String> changeUserEnabled(@PathVariable Long userId) {
        userService.changeUserEnabled(userId);
        return ResponseEntity.status(OK)
                .body("User enabled status changed");
    }
}