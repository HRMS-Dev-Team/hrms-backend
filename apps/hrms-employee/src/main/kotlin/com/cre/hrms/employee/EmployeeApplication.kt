package com.cre.hrms.employee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.cre.hrms.employee",
    "com.cre.hrms.security",
    "com.cre.hrms.messaging"
])
@EnableJpaRepositories(basePackages = [
    "com.cre.hrms.persistence.employee",
    "com.cre.hrms.persistence.department"
])
@EntityScan(basePackages = [
    "com.cre.hrms.persistence.employee",
    "com.cre.hrms.persistence.department"
])
class EmployeeApplication

fun main(args: Array<String>) {
    runApplication<EmployeeApplication>(*args)
}
