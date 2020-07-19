plugins {
    `kotlin-dsl`
    groovy
    `maven-publish`
    distribution
}

repositories {
    jcenter()
}
dependencies{
    gradleApi()
    localGroovy()
}


gradlePlugin {
    // Define the plugin
    val clickPlugin by plugins.creating {
        id = "com.hzy.pluginmodule.ClickPlugin"
        implementationClass = "com.hzy.pluginmodule.ClickPlugin"
    }
}

//上传服务

//distributions {
//    create("custom") {
//        // configure custom distribution
//    }
//}

//publishing {
//    publications {
//        create<MavenPublication>("PluginModule") {
//            artifact(tasks.distZip.get())
//            artifact(tasks["customDistTar"])
//        }
//    }
//}




