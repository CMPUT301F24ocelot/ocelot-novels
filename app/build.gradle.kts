plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.ocelotnovels"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ocelotnovels"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
}

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}


dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.recyclerview)
    implementation(libs.espresso.intents)
    implementation(libs.fragment.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation("com.google.firebase:firebase-messaging:24.1.0")
    // https://mvnrepository.com/artifact/com.google.zxing/javase
    implementation("com.google.zxing:javase:3.5.3")
    // https://mvnrepository.com/artifact/com.google.zxing/core
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    implementation("com.google.android.gms:play-services-tflite-java:16.2.0-beta02")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation ("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.test:core:1.4.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") // For RecyclerView actions
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")
    // Firebase Firestore and related libraries
    implementation("com.google.protobuf:protobuf-javalite") // Ensure this is compatible with Firebase

    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
}