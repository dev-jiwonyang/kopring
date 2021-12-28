import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
	id("org.springframework.boot") version "2.6.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.google.protobuf") version "0.8.17" 				// protobuf plugin : proto 파일을 java 파일로 변환하는 프로토콜 버퍼 컴파일러(protoc)
	id ("org.jetbrains.kotlin.plugin.jpa") version "1.3.61" // No-arg compiler plugin : @Entity, @Embeddable, @MappedSuperClass 어노테이션이 붙어있는 클래스에 No-arg 생성자를 자동으로 생성
	kotlin("jvm") version "1.6.0"
	kotlin("plugin.spring") version "1.6.0"
	kotlin("plugin.allopen") version "1.4.32" 			// 기본적으로 코틀린은 클래스가 final이기 때문에 spring 에서 @Configuration, @Transactional 같은 AOP를 사용하기 어려워서 Spring annotaion(아래 allOpen에서 지정)에 대해서 open 을 허용하는 플러그인을 추가
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")

	/* kotlin */
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin") // 코틀린 클래스와 data class 에 대한 직렬화 및 역직렬화 지원

	/* db + jpa */
	implementation ("org.postgresql:postgresql:42.2.18")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	/* grpc */
	implementation("com.google.protobuf:protobuf-java:3.17.3")          // protocol buffer를 java 파일로 컴파일
	implementation("io.grpc:grpc-protobuf:1.39.0")                      // protobuf-java로 만들어지는 서버 입장의 파일에서 필요한 메서드 등을 포함
	implementation("io.grpc:grpc-stub:1.39.0")                          // protobuf-java로 만들어지는 클라이언트 입장의 파일에서 필요한 메서드 등을 포함
	api("org.apache.tomcat:annotations-api:6.0.53")                     // grpc를 사용할 때 java 9 이상에서 사용하기 위해서 필요한 의존성

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

sourceSets{
	getByName("main"){
		java {
			srcDirs(
				"build/generated/source/proto/main/grpc",
				"build/generated/source/proto/main/java"
			)
		}
	}
}

protobuf {
	protoc { // protocol buffer를 컴파일하는 protoc의 스펙을 지정
		artifact = "com.google.protobuf:protoc:3.17.3"
	}

	plugins {
		// 컴파일 과정중 추가할 부분, 해당 프로젝트는 grpc 모델이 만들어여야하므로 아래의 설정이 있음.
		id("grpc") {
			// artifact = "io.grpc:protoc-gen-grpc-java:1.39.0"
			artifact = "'io.grpc:protoc-gen-grpc-kotlin:1.2.0'"
		}
	}

	generateProtoTasks {
		ofSourceSet("main").forEach {
			it.plugins {
				id("grpc")
			}
		}
	}
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
