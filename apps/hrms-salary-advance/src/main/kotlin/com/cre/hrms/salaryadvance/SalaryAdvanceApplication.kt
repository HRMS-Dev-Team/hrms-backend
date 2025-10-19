package com.cre.hrms.salaryadvance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.cre.hrms.salaryadvance",
    "com.cre.hrms.security"
])
@EnableJpaRepositories(basePackages = ["com.cre.hrms.persistence.salaryadvance"])
@EntityScan(basePackages = ["com.cre.hrms.persistence.salaryadvance"])
class SalaryAdvanceApplication

fun main(args: Array<String>) {
    runApplication<SalaryAdvanceApplication>(*args)
}
