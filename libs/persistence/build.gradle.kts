plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":libs:core"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
}
