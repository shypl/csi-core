plugins {
	kotlin("jvm") version "2.1.10" apply false
	id("maven-publish")
	id("nebula.release") version "19.0.10"
}

subprojects {
	afterEvaluate {
		
		extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>()?.apply {
			jvmToolchain(21)
			
			repositories {
				mavenCentral()
				maven("https://maven.pkg.github.com/shypl/packages").credentials {
					username = ""
					password = project.property("shypl.gpr.key") as String
				}
			}
		}
		
		extensions.findByType<PublishingExtension>()?.apply {
			group = "org.shypl.csi"
			
			publications.create<MavenPublication>("Library") {
				from(components["java"])
			}
			configure<JavaPluginExtension> {
				withSourcesJar()
			}
		}
	}
}

publishing {
	repositories.maven("https://maven.pkg.github.com/shypl/packages").credentials {
		username = project.property("shypl.gpr.user") as String
		password = project.property("shypl.gpr.key") as String
	}
}

tasks.release {
	finalizedBy(tasks["publish"])
}
