// Java pur, sans framework : seul JUnit, pour les tests.
plugins {
    application
}

group = "fr.annway"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "fr.annway.Main"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // Tout avertissement du compilateur fait échouer le build.
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.test {
    useJUnitPlatform()
}
