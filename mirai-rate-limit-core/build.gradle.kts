plugins {
    kotlin("jvm")
    id("me.him188.maven-central-publish")
}

version = "1.2.0"

val miraiVersion: String by lazy {
    rootProject.buildscript.configurations.getByName("classpath").dependencies
        .first { it.name == "net.mamoe.mirai-console.gradle.plugin" }
        .version!!
}

dependencies {
    api("net.mamoe:mirai-core-api:${miraiVersion}")

    testImplementation(kotlin("test-junit5"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

mavenCentralPublish {
    useCentralS01()

    singleDevGithubProject("ryoii", projectName)
    licenseFromGitHubProject("AGPL-3.0", "master")
}