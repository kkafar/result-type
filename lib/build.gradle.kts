import org.gradle.initialization.Environment.Properties

plugins {
    `java-library`
    `maven-publish`
}

group = "com.kkafara.rt"
version = "1.0.3"

java {
    withJavadocJar()
    withSourcesJar()
}

fun getPropertyOrEnv(propName: String, envVarName: String?): String? {
    return (project.findProperty(propName) as String? ?: System.getenv(envVarName))
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kkafar/result-type")
            credentials {
                username = getPropertyOrEnv("gh.user", "GITHUB_USERNAME")
                password = getPropertyOrEnv("gh.token", "GITHUB_TOKEN")
            }
        }
        maven {
            name = "JFrogBintray"
            url = uri("https://kkafara.jfrog.io/artifactory/kkafara-gradle-release-local")
            credentials {
                username = getPropertyOrEnv("jfrog.user", "JFROG_USERNAME")
                password = getPropertyOrEnv("jfrog.token", "JFROG_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            groupId = "com.kkafara.rt"
            artifactId = "result-type"
            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("org.jetbrains:annotations:23.0.0")
}
