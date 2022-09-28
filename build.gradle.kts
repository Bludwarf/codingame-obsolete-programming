import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    application
}

group = "com.codingame"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}

tasks.register("codingame") {
//    dependsOn("build") // TODO does not work, see tasks.named("build") { finalizedBy("codingame") } below

    // Inspired by https://stackoverflow.com/a/47422803/1655155
    inputs.files(fileTree("src/main/kotlin")).skipWhenEmpty()
    outputs.file("${project.buildDir}/codingame/Main.kt")
    doLast {
        outputs.files.singleFile.bufferedWriter().use { writer ->

            // TODO create enum for importLine & otherLine
            val linePairs: List<Pair<String, String>> = inputs.files.flatMap {file ->
                file.reader().use { reader ->
                    reader.readLines().mapNotNull { line ->
                        if (line.startsWith("package")) {
                            // Strip package line
                            null
                        } else if (line.startsWith("import")) {
                            // TODO en fait il faut plutôt garder uniquement les import autorisés : java.* et kotlin.*
                            val imported = line.substringAfter("import").trim()
                            if (imported.startsWith("java.") || imported.startsWith("kotlin.")) {
                                "importLine" to line
                            } else {
                                null
                            }
                        } else {
                            "otherLine" to line
                        }
                    }
                }
            }

            val categorizedLines: Map<String, List<String>> = linePairs
                .groupingBy { it.first }
                .aggregate { _, acc, element, _ ->
                    val list = acc ?: emptyList()
                    list + element.second
                }

            categorizedLines["importLine"]?.let { importLines ->
                importLines.toSortedSet().forEach {
                    writer.appendLine(it)
                }
            }
            categorizedLines["otherLine"]?.forEach {
                writer.appendLine(it)
            }
        }
    }
}

// Source : https://handstandsam.com/2021/06/07/run-custom-gradle-task-after-build/
// Indeed dependsOn("build") does not work in tasks.register("codingame") lambda
// To bind task with Build in IntelliJ : Gradle panel > <project> > Tasks > other > codingame > Execute After Build
tasks.named("build") { finalizedBy("codingame") }
