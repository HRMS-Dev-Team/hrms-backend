package com.cre.hrms.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * Custom UserDetails implementation that includes employee ID from JWT claims.
 * Used in microservices that don't have direct access to the user database.
 */
public class JwtUserDetails extends User {
    private final UUID employeeId;
    private final String firstName;
    private final String lastName;

    public JwtUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            UUID employeeId,
            String firstName,
            String lastName
    ) {
        super(username, password, authorities);
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
