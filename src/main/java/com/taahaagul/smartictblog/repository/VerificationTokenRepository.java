package com.taahaagul.smartictblog.repository;

import com.taahaagul.smartictblog.entity.User;
import com.taahaagul.smartictblog.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findByUser(User user);
}
