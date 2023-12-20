package com.taahaagul.smartictblog.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taahaagul.smartictblog.config.JwtService;
import com.taahaagul.smartictblog.entity.*;
import com.taahaagul.smartictblog.exception.IncorrectValueException;
import com.taahaagul.smartictblog.exception.ResourceAlreadyExistsException;
import com.taahaagul.smartictblog.exception.ResourceNotFoundException;
import com.taahaagul.smartictblog.repository.TokenRepository;
import com.taahaagul.smartictblog.repository.UserRepository;
import com.taahaagul.smartictblog.repository.VerificationTokenRepository;
import com.taahaagul.smartictblog.request.ForgetPaswRequest;
import com.taahaagul.smartictblog.request.LoginRequest;
import com.taahaagul.smartictblog.request.RegisterRequest;
import com.taahaagul.smartictblog.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Register a new user with the credentials
     * @param request
     */
    public void register(RegisterRequest request) {
        Optional<User> existingUserName = userRepository.findByUserName(request.getUserName());
        if(existingUserName.isPresent())
            throw new IncorrectValueException("Username already exist!");

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new IncorrectValueException("Email already exist!");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .memberSince(LocalDate.now())
                .role(Role.USER)
                .enabled(false)
                .build();
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    /**
     * Generate a verification token and delete the old tokens.
     * @param user
     * @return token
     */
    public String generateVerificationToken(User user) {
        if(user.isEnabled())
            revokeAllVerificationToken(user);

        String token = UUID.randomUUID().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 4);
        Date expirationTime = calendar.getTime();

        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expirationTime(expirationTime)
                .build();

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    /**
     * Verify the user account with the verification token
     * @param token
     */
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new ResourceNotFoundException("Verification Token", "token", token)));
    }

    /**
     * Fetch the user and enable the account and delete the old tokens
     * @param verificationToken
     */
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User  user = userRepository.findByEmail(username)
                .orElseThrow(()-> new ResourceNotFoundException("User", "username", username));
        user.setEnabled(true);
        revokeAllVerificationToken(user);
        userRepository.save(user);
    }

    /**
     * Authenticate the user
     * @param request
     * @return access token and refresh token
     */
    public AuthenticationResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Clear all user tokens in the database
     * @param user
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        tokenRepository.deleteAll(validUserTokens);
    }

    /**
     * Clear all verification tokens in the database
     * @param user
     */
    private void revokeAllVerificationToken(User user) {
        var verificationTokens = verificationTokenRepository.findByUser(user);
        if (verificationTokens.isEmpty())
            return;
        verificationTokenRepository.deleteAll(verificationTokens);
    }

    /**
     * Save the user token in the database
     * @param user
     * @param jwtToken
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Refresh the access token using refresh token
     * @param request
     * @param response
     * @throws IOException
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    /**
     * Get the current user with SecurityContextHolder
     */
    public User getCurrentUser() {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", "email"));
        return user;
    }

    /**
     * Getting verification token for reset password, expire time 4 minutes
     * @param email
     */
    public void forgetMyPaswToken(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String token = generateVerificationToken(existingUser);

        mailService.sendMail(new NotificationEmail("Forget My Password",
                existingUser.getEmail(), "Please copy this token = " + token));
    }

    /**
     * Change password with verificationtoken
     * @param forgetPaswRequest
     */
    public void forgetChangePasw(ForgetPaswRequest forgetPaswRequest) {

        VerificationToken verificationToken = verificationTokenRepository
                .findByToken(forgetPaswRequest.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("VerificationToken", "Token", forgetPaswRequest.getToken()));

        Date now = new Date();

        if(verificationToken.getExpirationTime().after(now)) {
            User user = verificationToken.getUser();
            user.setPassword(passwordEncoder.encode(forgetPaswRequest.getNewPasw()));
            userRepository.save(user);
        } else {
            throw new IncorrectValueException("Token is expired!");
        }
    }
}
