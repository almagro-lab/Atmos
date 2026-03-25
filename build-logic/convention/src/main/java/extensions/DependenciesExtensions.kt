package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(project: Project, dependency: String) {
    add("implementation", project.libs.get(dependency))
}

internal fun DependencyHandler.ksp(project: Project, dependency: String) {
    add("ksp", project.libs.get(dependency))
}

internal fun DependencyHandler.testImplementation(project: Project, dependency: String) {
    add("testImplementation", project.libs.get(dependency))
}

internal fun DependencyHandler.testRuntimeOnly(project: Project, dependency: String) {
    add("testRuntimeOnly", project.libs.get(dependency))
}