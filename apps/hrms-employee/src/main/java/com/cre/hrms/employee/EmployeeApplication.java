package com.cre.hrms.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.cre.hrms.employee",
    "com.cre.hrms.common",
    "com.cre.hrms.core",
    "com.cre.hrms.persistence",
    "com.cre.hrms.security",
    "com.cre.hrms.config"
})
public class EmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }
}
