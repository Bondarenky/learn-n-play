package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.EmailVerificationToken;
import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.exception.BadRequestException;
import com.bodanka.learnnplay.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultEmailVerificationTokenService implements EmailVerificationTokenService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserService userService;

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return emailVerificationTokenRepository.findByToken(token);
    }

    @Override
    public EmailVerificationToken generateEmailVerificationToken(User user) {
        return emailVerificationTokenRepository.save(
                new EmailVerificationToken(UUID.randomUUID().toString(), user)
        );
    }

    @Override
    @Transactional
    public boolean validateEmailVerificationToken(String token) {
        EmailVerificationToken emailVerificationToken = findByToken(token).orElseThrow(() -> {
            return new BadRequestException("Email verification token [%s] not found".formatted(token));
        });

        if (emailVerificationToken.getConfirmedAt() != null) {
            throw new BadRequestException("Email is already confirmed");
        }
        if (emailVerificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Email verification token is expired");
        }

        emailVerificationTokenRepository.confirm(token, LocalDateTime.now());
        userService.enable(UUID.fromString(emailVerificationToken.getUser().getId()));
        return true;
    }
}
