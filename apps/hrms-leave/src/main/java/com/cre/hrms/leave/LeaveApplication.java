package com.cre.hrms.leave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.cre.hrms.leave",
    "com.cre.hrms.common",
    "com.cre.hrms.core",
    "com.cre.hrms.persistence",
    "com.cre.hrms.security",
    "com.cre.hrms.config"
})
public class LeaveApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaveApplication.class, args);
    }
}
