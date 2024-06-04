package com.bodanka.learnnplay.service;

import com.bodanka.learnnplay.domain.entity.User;

public interface AuthService {
    User signUp(User user);

    boolean verifyEmailVerificationToken(String token);

    String signIn(String email, String password);
}
