import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    `maven-publish`
    signing
}

group = "io.pexkit"
version = "0.1.0"

kotlin {
    explicitApi()

    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "PexKit"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }
    }
}

android {
    namespace = "io.pexkit.api"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// Publishing configuration
publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("PexKit")
            description.set("Kotlin Multiplatform client library for the Pexels API")
            url.set("https://github.com/G10xy/pexkit")

            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }

            developers {
                developer {
                    id.set("G10xy")
                    name.set("Giovanni")
                    email.set("giovanni.desiderio@proton.me")
                }
            }

            scm {
                url.set("https://github.com/G10xy/pexkit")
                connection.set("scm:git:git://github.com/G10xy/pexkit.git")
                developerConnection.set("scm:git:ssh://github.com/G10xy/pexkit.git")
            }
        }
    }

    repositories {
        maven {
            name = "MavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("mavenCentralUsername") as String? ?: System.getenv("MAVEN_CENTRAL_USERNAME")
                password = findProperty("mavenCentralPassword") as String? ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as String? ?: System.getenv("SIGNING_KEY")
    val signingPassword = findProperty("signingPassword") as String? ?: System.getenv("SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
