plugins {
    id("java")
    application
}

group = "de.aventix"
version = "1.0-SNAPSHOT"

application {
    mainClass = "de.aventix.bot.DiscordBotApplication"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.inject", "guice", "5.1.0")
    implementation("com.google.code.gson", "gson", "2.9.0")
    implementation("net.dv8tion", "JDA", "5.0.0-beta.20")
    implementation("com.github.twitch4j", "twitch4j", "1.19.0")
    implementation("org.slf4j", "slf4j-nop", "2.0.11")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.13")
    compileOnly("org.projectlombok", "lombok", "1.18.30")
    annotationProcessor("org.projectlombok", "lombok", "1.18.30")
}