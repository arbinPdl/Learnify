package com.learnify.backend.security;

import com.learnify.backend.entity.User;
import com.learnify.backend.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final UserRole userRole;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = !user.isDeleted();
        this.userRole = user.getUserRole();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
    }

    public Long getUserId() {
        return userId;
    }

    public UserRole getUserRole() {
        return userRole;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
