plugins {
	kotlin("jvm")
	id("java-library")
	id("maven-publish")
}

dependencies {
	implementation(libs.shypl.tool.lang)
	implementation(libs.shypl.tool.utils)
	implementation(libs.shypl.tool.io)
	implementation(libs.shypl.tool.logging)
	api(project(":csi-core-common"))
	
	testImplementation(kotlin("test"))
	testImplementation(libs.logback)
}
