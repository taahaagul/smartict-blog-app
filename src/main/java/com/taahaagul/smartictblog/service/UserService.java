package com.taahaagul.smartictblog.service;

import com.taahaagul.smartictblog.entity.Role;
import com.taahaagul.smartictblog.entity.User;
import com.taahaagul.smartictblog.exception.IncorrectValueException;
import com.taahaagul.smartictblog.exception.ResourceNotFoundException;
import com.taahaagul.smartictblog.repository.UserRepository;
import com.taahaagul.smartictblog.request.UserChangePaswRequest;
import com.taahaagul.smartictblog.request.UserUpdateRequest;
import com.taahaagul.smartictblog.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current user with SecurityContextHolder
     * @return UserResponse
     */
    public UserResponse getCurrentUser() {
        return new UserResponse(authenticationService.getCurrentUser());
    }

    /**
     * Get all users with pagination
     * @param request
     * @return Page<UserResponse>
     */
    public void changePassword(UserChangePaswRequest request) {
        User user = authenticationService.getCurrentUser();
        if(passwordEncoder.matches(request.getOldPasw(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPasw()));
            userRepository.save(user);
        } else
            throw new IncorrectValueException("Old Password is incorrect!");
    }

    /**
     * Update current user
     * @param request
     * @return UserResponse
     */
    public UserResponse updateUser(UserUpdateRequest request) {
        User foundUser = authenticationService.getCurrentUser();

        foundUser.setFirstName(request.getFirstName());
        foundUser.setLastName(request.getLastName());
        foundUser.setUserName(request.getUserName());
        foundUser.setEmail(request.getEmail());

        return new UserResponse(userRepository.save(foundUser));
    }

    /**
     * update user role
     * @param userId
     * @param userRole
     */
    public void updateUserRole(Long userId, String userRole) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        foundUser.setRole(Role.valueOf(userRole));
        userRepository.save(foundUser);
    }

    /**
     * change user enabled status
     * @param userId
     */
    public void changeUserEnabled(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));

        if(foundUser.isEnabled())
            foundUser.setEnabled(false);
        else
            foundUser.setEnabled(true);

        userRepository.save(foundUser);
    }

    /**
     * Get user by id
     * @param userId
     * @return UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse getAnyUser(Long userId) {
        User foundUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId.toString()));;

        return new UserResponse(foundUser);
    }

    /**
     * Get all users with pagination
     * @param pageable
     * @return Page<UserResponse>
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserResponse::new);
    }
}
