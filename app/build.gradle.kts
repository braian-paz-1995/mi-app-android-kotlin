plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.android.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ationet.androidterminal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ationet.androidterminal"
        minSdk = 21
        targetSdk = 34
        versionCode = 36
        versionName = "2.1.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "platform"
    productFlavors {
        create("newland") {
            dimension = "platform"
            applicationIdSuffix = ".newland"
        }

        create("urovo") {
            dimension = "platform"
            applicationIdSuffix = ".urovo"
        }

        create("t650p") {
            dimension = "platform"
            applicationIdSuffix = ".t650p"
            minSdk = 27
        }
        create("AndroidPhone") {
            dimension = "platform"
            applicationIdSuffix = ".androidPhone"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {


    //
    val newlandImplementation = "newlandImplementation"
    val urovoImplementation = "urovoImplementation"
    val t650pImplementation = "t650pImplementation"
    val androidPhoneImplementation = "androidPhoneImplementation"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.appcompat)
    testImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test"))

    newlandImplementation(files("libs/MESDK-3.10.54-RELEASE.aar"))
    urovoImplementation(files("libs/urovosdkLibs_New_v1.0.3.aar"))
    t650pImplementation(files("libs/MESDK-3.10.54-RELEASE.aar"))
    t650pImplementation(libs.androidx.room.common.jvm)
    t650pImplementation(files("libs/PaymentSDK-3.68.3-sdi.aar"))
    t650pImplementation(files("libs/UpdateServiceLib-0.1.271.aar"))
    t650pImplementation(files("libs/UsbConnManLib-1.0.63.aar"))
    t650pImplementation(libs.androidx.junit.ktx)


    /* Log Module */
    implementation(project(":log"))

    /* Terminal Management Module */
    implementation(files("libs/AtioSyncKit-release-0.0.3.0.aar"))

    /* EMV NFC Pay card Enrollment */
    implementation(libs.devnied.emvnfccard.library)

    /* Hilt */
    implementation(libs.androidx.hilt.common)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    /* Hilt navigation compose */
    implementation(libs.androidx.hilt.navigation.compose)

    /* Kotlin Serialization */
    implementation(libs.kotlinx.serialization.json)

    /* Ktor */
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    /* DataStore */
    implementation(libs.androidx.datastore.preferences)

    /* Constraint Layout*/
    implementation(libs.androidx.constraintlayout.compose)

    /* Lottie Animation */
    implementation(libs.lottie.compose)

    /* Navigation compose */
    implementation(libs.androidx.navigation.compose)

    /* Room */
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)

    /* Paging */
    implementation(libs.androidx.paging)
    implementation(libs.androidx.paging.ktx)
    implementation(libs.androidx.paging.compose)

    /* Camera */
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)

    /* Date-time */
    implementation(libs.kotlinx.datetime)

    /* QR and camera */
    implementation(libs.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.barcode.scanning)
    implementation(libs.androidx.camera.mlkit.vision)

    /* Desugar JDK libs */
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    /* Camera 2*/
    implementation(libs.androidx.camera.camera2)

    /* Worker */
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation ("com.google.code.gson:gson:2.10.1")


}

class RoomSchemaArgProvider(
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    private val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}