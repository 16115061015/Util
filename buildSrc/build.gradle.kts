plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
    google()
}

dependencies{
    //自定义插件导入jar
    gradleApi()
    //自定义Transfrom工具包
    implementation("com.android.tools.build:gradle:3.6.1")
}

