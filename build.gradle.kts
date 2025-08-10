plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.1"
    application
}

group = "org.cft"
version = "1.0.0"

application {
    mainClass.set("org.cft.FilterApp")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.7")

    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }


    shadowJar {
        archiveBaseName.set("filter-util")
        archiveClassifier.set("")    
        archiveVersion.set(project.version.toString())
        mergeServiceFiles()          
        manifest {
            attributes("Main-Class" to "org.cft.FilterApp")
        }
    }

    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }
}