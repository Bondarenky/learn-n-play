package com.bodanka.learnnplay.security;

import com.bodanka.learnnplay.domain.entity.User;
import com.bodanka.learnnplay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User with email [%s] not found".formatted(username))
        );

        return DefaultUserDetails.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())))
                .password(user.getPassword())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .build();
    }
}
