plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.android)
}

gradlePlugin {
    plugins {
        register("hiltConventionPlugin") {
            id = "hilt.convention.plugin"
            implementationClass = "plugins.android.HiltConventionPlugin"
        }
        register("unitTestConventionPlugin") {
            id = "unit.test.convention.plugin"
            implementationClass = "plugins.analysis.UnitTestConventionPlugin"
        }
    }
}