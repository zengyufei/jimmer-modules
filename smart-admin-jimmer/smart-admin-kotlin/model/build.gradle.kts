import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

val jimmerVersion: String by rootProject.extra
val springBootVersion: String by rootProject.extra

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
}


dependencies {

    api("cn.dev33:sa-token-spring-boot3-starter:1.37.0")
    api("cn.dev33:sa-token-redis-jackson:1.37.0")
    api("org.bouncycastle:bcprov-jdk18on:1.79")



    api("org.lionsoul:ip2region:2.7.0")
    api("cn.hutool:hutool-all:5.8.29")
    api("io.github.microutils:kotlin-logging-jvm:2.0.6")
    api("org.apache.commons:commons-lang3:3.12.0")
    api("commons-codec:commons-codec:1.13")
    api("org.springframework.boot:spring-boot-starter-data-redis:${springBootVersion}")
    api("org.springframework.boot:spring-boot-starter-validation:${springBootVersion}")

    implementation("org.babyfish.jimmer:jimmer-sql-kotlin:${jimmerVersion}")
    ksp("org.babyfish.jimmer:jimmer-ksp:${jimmerVersion}")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
}

// Without this configuration, gradle command can still run.
// However, Intellij cannot find the generated source.
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
