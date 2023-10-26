plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("me.him188.maven-central-publish") version "1.0.0"
    id("net.mamoe.mirai-console") version "2.15.0" apply false
}

group = "cn.ryoii"
version = "1.1.0"

allprojects {
    group = "cn.ryoii"

    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}