plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    // Lib dependencies
    implementation(project(":libs:core"))
    implementation(project(":libs:persistence"))
    implementation(project(":libs:dto"))
    implementation(project(":libs:security"))
    implementation(project(":libs:config"))
    implementation(project(":libs:messaging"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Testing
    testImplementation(project(":libs:test-utils"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.bootJar {
    archiveFileName.set("hrms-auth.jar")
}
