pluginManagement {
    repositories {
        mavenLocal()
        maven { setUrl("https://mirrors.tencent.com/nexus/repository/maven-public") }
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven") }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/jcenter")
        }
        maven {
            setUrl("https://maven.aliyun.com/nexus/content/repositories/releases")
        }
        maven {
            setUrl("https://jitpack.io")
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenLocal()
        maven { setUrl("https://mirrors.tencent.com/nexus/repository/maven-public") }
        maven { setUrl("https://mirrors.huaweicloud.com/repository/maven") }
        maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/google")
        }
        maven {
            setUrl("https://maven.aliyun.com/repository/jcenter")
        }
        maven {
            setUrl("https://maven.aliyun.com/nexus/content/repositories/releases")
        }
        maven {
            setUrl("https://jitpack.io")
        }
        google()
        mavenCentral()
    }
}



rootProject.name = "zyfAdmin"
include("model")
include("runtime")
include("repository")
include("service")


