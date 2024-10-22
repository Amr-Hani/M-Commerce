plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}


android {

    namespace = "com.example.mcommerce"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mcommerce"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
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

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)

    implementation(libs.androidx.legacy.support.v4)

    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.fragment.ktx)

    implementation(libs.play.services.maps)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //FireBase Authentication
    implementation ("com.google.firebase:firebase-bom:31.0.1")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:20.2.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // view model life cycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //ImageSlider
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

//    // أساسيات Android
//    implementation ("androidx.appcompat:appcompat:1.6.1")
//    implementation ("com.google.android.material:material:1.9.0")
//
//    // مكتبة ImageSlider
//    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")
//
//    // إذا كنت تستخدم Glide لتحميل الصور (اختياري ولكن موصى به)
//    implementation ("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.paypal.sdk:paypal-android-sdk:2.16.0"){exclude(group = "io.card")
        implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")


        implementation("com.paypal.android:paypal-web-payments:1.5.0")
//stripe for online payment
        implementation ("com.stripe:stripe-android:20.52.1")
        implementation("com.android.volley:volley:1.2.1")

}}

