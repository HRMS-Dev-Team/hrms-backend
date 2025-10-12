package com.cre.hrms.leave

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = [
    "com.cre.hrms.leave",
    "com.cre.hrms.security"
])
@EnableJpaRepositories(basePackages = ["com.cre.hrms.persistence.leave"])
@EntityScan(basePackages = ["com.cre.hrms.persistence.leave"])
class LeaveApplication

fun main(args: Array<String>) {
    runApplication<LeaveApplication>(*args)
}
