plugins {
    kotlin("jvm")
    id("me.him188.maven-central-publish")
    id("net.mamoe.mirai-console")
}

dependencies {

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