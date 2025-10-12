package com.cre.hrms.persistence.user.entity

import com.cre.hrms.core.enums.Role
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    private var username: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    private var password: String,

    @Column(name = "first_name")
    var firstName: String? = null,

    @Column(name = "last_name")
    var lastName: String? = null,

    @Column(name = "employee_id")
    var employeeId: java.util.UUID? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<Role> = mutableSetOf(),

    @Column(name = "is_enabled")
    private var enabled: Boolean = true,

    @Column(name = "is_account_non_expired")
    private var accountNonExpired: Boolean = true,

    @Column(name = "is_account_non_locked")
    private var accountNonLocked: Boolean = true,

    @Column(name = "is_credentials_non_expired")
    private var credentialsNonExpired: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {

    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }.toSet()
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled

    // Helper methods to update username and password (since they're private)
    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun setAccountNonExpired(accountNonExpired: Boolean) {
        this.accountNonExpired = accountNonExpired
    }

    fun setAccountNonLocked(accountNonLocked: Boolean) {
        this.accountNonLocked = accountNonLocked
    }

    fun setCredentialsNonExpired(credentialsNonExpired: Boolean) {
        this.credentialsNonExpired = credentialsNonExpired
    }
}
