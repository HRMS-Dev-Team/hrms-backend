package com.cre.hrms.salaryadvance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.cre.hrms.salaryadvance",
    "com.cre.hrms.common",
    "com.cre.hrms.core",
    "com.cre.hrms.persistence",
    "com.cre.hrms.security",
    "com.cre.hrms.config"
})
public class SalaryAdvanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SalaryAdvanceApplication.class, args);
    }
}
