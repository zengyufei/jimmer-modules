import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
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

	implementation(project(":model"))
	implementation(project(":repository"))

	implementation("com.github.penggle:kaptcha:2.3.2"){
		exclude("javax.servlet", "*")
	}
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")

	implementation("org.springframework.boot:spring-boot-starter-web:${springBootVersion}")
	implementation("org.redisson:redisson-spring-boot-starter:3.17.4")
//	implementation("org.springframework.kafka:spring-kafka:${springBootVersion}")
//	implementation("org.apache.kafka:connect-api:0.10.0.0")
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
