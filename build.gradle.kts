import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "dev.thezexquex"
version = "0.1.0"

val shadeBase = "$group.${rootProject.name.lowercase()}.libs"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation("cloud.commandframework", "cloud-paper", "1.8.4")
    implementation("org.spongepowered", "configurate-yaml", "4.0.0")

    compileOnly("io.papermc.paper", "paper-api", "1.20.1-R0.1-SNAPSHOT")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        archiveVersion.set(version.toString())

        relocate("cloud.commandframework", "$shadeBase.cloud")
        relocate("org.spongepowered", "$shadeBase.configurate")
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    register<Copy>("copyToServer") {
        val path = System.getenv("SERVER_DIR")
        if (path.toString().isEmpty()) {
            println("No SERVER_DIR env variable set")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}

bukkit {
    version = "0.1.0"
    author = "TheZexquex"
    apiVersion = "1.20"
    main = "$group.${rootProject.name.lowercase()}.${rootProject.name}Plugin"

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
    permissions {
        register("commandchain.command.chain")
    }
}
