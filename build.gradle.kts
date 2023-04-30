import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "cn.takamina.heaven"
version = project.version

plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.55"
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
}

taboolib {
    install("common")
    install("common-5")
    install("module-configuration")
    install("module-chat")
    install("module-database")
    install("module-lang")
    install("module-nms-util")
    install("module-nms")
    install("module-ui")
    install("module-metrics")
    install("platform-bukkit")
    install("module-kether")
    install("expansion-command-helper")
    install("expansion-player-database")
    install("expansion-javascript")
    classifier = null
    version = "6.0.10-98"
    relocate("org.bstats", "cn.takamina.heaven")
    description {
        contributors {
            name("Takamina")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
        }
        prefix("Heaven")
        desc("Heaven")
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11902:11902-minimize:mapped")
    compileOnly("ink.ptms.core:v11902:11902-minimize:universal")
    // implementation("org.spigotmc:spigot:1.15.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("org.javassist:javassist:3.29.2-GA")
    compileOnly("org.luaj:luaj-jse:3.0.1")
    compileOnly("org.python:jython-standalone:2.7.3")
    compileOnly("com.typesafe:config:1.4.2")
    compileOnly("io.github.config4k:config4k:0.5.0")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}