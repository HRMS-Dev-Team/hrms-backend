plugins {
    id("org.springframework.boot")
}

dependencies {
    // Lib dependencies
    implementation(project(":libs:persistence"))
    implementation(project(":libs:dto"))
    implementation(project(":libs:security"))
    implementation(project(":libs:config"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Testing
    testImplementation(project(":libs:test-utils"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    archiveFileName.set("hrms-employee.jar")
}
