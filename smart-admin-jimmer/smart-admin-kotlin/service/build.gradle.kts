import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	id("com.google.devtools.ksp") version "1.9.21-1.0.15"
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.0.12.RELEASE"
}

val jimmerVersion: String by rootProject.extra

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenLocal()
	mavenCentral()
	maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
}


dependencies {

	implementation(project(":repository"))
	implementation(project(":runtime"))

	ksp("org.babyfish.jimmer:jimmer-ksp:${jimmerVersion}")

	implementation("com.amazonaws:aws-java-sdk-s3:1.11.842"){
		exclude("commons-logging", "commons-logging")
	}
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")

	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.googlecode.concurrentlinkedhashmap:concurrentlinkedhashmap-lru:1.4.2")

//	runtimeOnly("com.h2database:h2:2.1.212")
//	runtimeOnly("mysql:mysql-connector-java:8.0.30")
	runtimeOnly("org.postgresql:postgresql:42.6.0")
	runtimeOnly("io.lettuce:lettuce-core:6.2.0.RELEASE")
	runtimeOnly("com.github.ben-manes.caffeine:caffeine:2.9.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
