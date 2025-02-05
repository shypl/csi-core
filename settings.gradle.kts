plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "csi-core"

include(
	"csi-core-common",
	"csi-core-frontend",
	"csi-core-backend"
)