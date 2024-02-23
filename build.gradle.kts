plugins {
    id("java")
    application
}

group = "de.aventix"
version = "1.0-SNAPSHOT"

application {
    mainClass = "de.aventix.bot.DiscordBotApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.twitch4j", "twitch4j","1.19.0")
    implementation("com.google.inject", "guice","5.1.0")
    implementation("com.google.code.gson", "gson","2.9.0")
    implementation("net.dv8tion", "JDA", "5.0.0-beta.20")
    implementation("org.projectlombok", "lombok","1.18.30")
    compileOnly("org.projectlombok", "lombok","1.18.30")
    annotationProcessor("org.projectlombok", "lombok","1.18.30")
}