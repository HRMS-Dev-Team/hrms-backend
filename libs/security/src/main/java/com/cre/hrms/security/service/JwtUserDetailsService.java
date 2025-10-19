package com.cre.hrms.security.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * JWT-based UserDetailsService for non-auth microservices.
 * This service creates UserDetails from username without database access,
 * as the JWT token itself provides authentication.
 *
 * For full user validation, the JWT signature verification in JwtAuthenticationFilter
 * is sufficient. This service just provides the UserDetails object needed by Spring Security.
 *
 * This bean is only created when no other UserDetailsService is available,
 * allowing auth service to use its own database-backed implementation.
 */
@Service
@ConditionalOnMissingBean(UserDetailsService.class)
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In microservices architecture, we don't have access to user database
        // The JWT token itself is the source of truth after signature verification
        // We create a minimal UserDetails with the username from the JWT
        // Roles/authorities should be extracted from JWT claims (to be implemented)
        return User.builder()
                .username(username)
                .password("") // No password needed as JWT is already validated
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }
}
