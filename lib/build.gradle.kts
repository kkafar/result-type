plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    api("org.apache.commons:commons-math3:3.6.1")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("org.jetbrains:annotations:23.0.0")
}
