package com.cre.hrms.employee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = [
    "com.cre.hrms.employee",
    "com.cre.hrms.security"
])
class EmployeeApplication

fun main(args: Array<String>) {
    runApplication<EmployeeApplication>(*args)
}
