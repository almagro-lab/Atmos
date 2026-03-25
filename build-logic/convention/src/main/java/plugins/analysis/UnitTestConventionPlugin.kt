package plugins.analysis

import extensions.testImplementation
import extensions.testRuntimeOnly
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class UnitTestConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }

            dependencies {
                testImplementation(project, "junit-jupiter")
                testRuntimeOnly(project, "junit-platform-launcher")
                testImplementation(project, "kotlinx-coroutines-test")
                testImplementation(project, "mockk")
                testImplementation(project, "turbine")
            }
        }
    }
}