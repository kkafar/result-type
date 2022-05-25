plugins {
    `java-library`
    `maven-publish`
}

group = "com.kkafara.rt"
version = "1.0.1"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kkafar/result-type")
            credentials {
                username = (project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME"))
                password = (project.findProperty("gpr.access_token") as String? ?: System.getenv("GITHUB_TOKEN"))
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
