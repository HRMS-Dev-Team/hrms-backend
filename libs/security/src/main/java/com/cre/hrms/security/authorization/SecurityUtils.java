package com.cre.hrms.security.authorization;

import com.cre.hrms.persistence.user.entity.User;
import com.cre.hrms.security.jwt.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SecurityUtils {

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getUsername());
        }
        return Optional.empty();
    }

    public static Optional<UserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return Optional.of((UserDetails) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public static Collection<String> getCurrentUserRoles() {
        return getCurrentUser()
                .map(UserDetails::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static boolean hasRole(String role) {
        return getCurrentUserRoles().stream()
                .anyMatch(r -> r.equals("ROLE_" + role) || r.equals(role));
    }

    public static boolean hasAnyRole(String... roles) {
        Collection<String> userRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (userRoles.stream().anyMatch(r -> r.equals("ROLE_" + role) || r.equals(role))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               authentication.getPrincipal() instanceof UserDetails;
    }

    public static Optional<UUID> getCurrentEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                return Optional.ofNullable(user.getEmployeeId());
            } else if (authentication.getPrincipal() instanceof JwtUserDetails) {
                JwtUserDetails jwtUser = (JwtUserDetails) authentication.getPrincipal();
                return Optional.ofNullable(jwtUser.getEmployeeId());
            }
        }
        return Optional.empty();
    }

    public static UUID getCurrentEmployeeIdOrThrow() {
        return getCurrentEmployeeId()
                .orElseThrow(() -> new RuntimeException("No employee ID found for current user"));
    }

    public static Optional<String> getCurrentEmployeeName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                String firstName = user.getFirstName();
                String lastName = user.getLastName();

                if (firstName != null && lastName != null) {
                    return Optional.of(firstName + " " + lastName);
                } else if (firstName != null) {
                    return Optional.of(firstName);
                } else if (lastName != null) {
                    return Optional.of(lastName);
                }
            } else if (authentication.getPrincipal() instanceof JwtUserDetails) {
                JwtUserDetails jwtUser = (JwtUserDetails) authentication.getPrincipal();
                String firstName = jwtUser.getFirstName();
                String lastName = jwtUser.getLastName();

                if (firstName != null && lastName != null) {
                    return Optional.of(firstName + " " + lastName);
                } else if (firstName != null) {
                    return Optional.of(firstName);
                } else if (lastName != null) {
                    return Optional.of(lastName);
                }
            }
        }
        return Optional.empty();
    }
}
