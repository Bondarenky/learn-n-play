package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.EmailVerificationToken;
import com.bodanka.learnnplay.domain.entity.User;

import java.util.Optional;

public interface EmailVerificationTokenService {
    Optional<EmailVerificationToken> findByToken(String token);

    EmailVerificationToken generateEmailVerificationToken(User user);

    boolean validateEmailVerificationToken(String token);
}
