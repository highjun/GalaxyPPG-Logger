import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension


internal class AndroidComposeApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            with(pluginManager){
                apply(libs.findPlugin("androidApplication").get().get().pluginId)
                apply(libs.findPlugin("ksp").get().get().pluginId)
                apply(libs.findPlugin("jetbrainsKotlinAndroid").get().get().pluginId)
                apply(libs.findPlugin("composeCompiler").get().get().pluginId)
            }
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    namespace= "kaist.iclab.galaxyppglogger"
                    minSdk = 21
                    targetSdk = 35
                    compileSdk = 35
                    applicationId = namespace // Equal name for namespace and applicationId
                }

                buildFeatures {
                    compose = true
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                extensions.getByType<ComposeCompilerGradlePluginExtension>().apply {
                    includeSourceInformation.set(true)
                }

                extensions.getByType<KotlinAndroidProjectExtension>().apply {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                packaging {
                    resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }

            dependencies {
                /* Basic Android Library + Coroutine */
                "implementation"(libs.findBundle("androidx").get())
                "implementation"(libs.findBundle("coroutines").get())

                /* Android Compose Library */
                "api"(platform(libs.findLibrary("androidx_compose_bom").get()))
                "implementation"(libs.findBundle("compose").get())

                /* Persistence Storage Library */
                "implementation"(libs.findBundle("room").get())
                "ksp"(libs.findLibrary("androidx_room_compiler").get())

                /* Dependency Injection (DI) Library */
                "implementation"(libs.findBundle("koin").get())

                /* Communication Channel between Smartphone and Watch */
                "implementation"(libs.findLibrary("google_play_services_wearable").get())
            }
        }
    }
}
