import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType


internal class AndroidComposeApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            with(pluginManager){
                apply(libs.findPlugin("androidApplication").get().get().pluginId)
                apply(libs.findPlugin("ksp").get().get().pluginId)
                apply(libs.findPlugin("jetbrainsKotlinAndroid").get().get().pluginId)
            }
            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    targetSdk = 35
                    applicationId = namespace // Equal name for namespace and applicationId
                }

                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.15"
                }

                packaging {
                    resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }

            dependencies {
                "implementation"(libs.findBundle("androidx").get())
                "implementation"(libs.findBundle("coroutines").get())

                "api"(platform(libs.findLibrary("androidx_compose_bom").get()))
                "implementation"(libs.findBundle("compose").get())

                "implementation"(libs.findBundle("room").get())
                "ksp"(libs.findLibrary("androidx_room_compiler").get())

                "implementation"(libs.findBundle("koin").get())
            }
        }
    }
}
