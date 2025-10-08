package com.cre.hrms.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.cre.hrms.auth",
    "com.cre.hrms.security"
])
@EntityScan(basePackages = ["com.cre.hrms.persistence.user.entity"])
@EnableJpaRepositories(basePackages = ["com.cre.hrms.persistence.user.repository"])
class AuthApplication

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}
