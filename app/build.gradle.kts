    plugins {
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("com.google.gms.google-services")
        // Versão do KSP compatível com Kotlin 1.9.0
        id("com.google.devtools.ksp")
    }

android {
    namespace = "np.com.pedrolborges.quizonline"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pedrolborges.quizonline"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BOM garante compatibilidade
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))

    // Firebase APIs
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Coroutines e Lifecycle (Necessário para os erros de lifecycleScope e Dispatchers)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.google.code.gson:gson:2.10.1") // Adicione esta linha

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Para Kotlin use kapt ou ksp (recomendo adicionar o plugin kapt no topo do arquivo)
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}