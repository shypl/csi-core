rootProject.name = "csi-core"

include(
	"csi-core-common",
	"csi-core-client",
	"csi-core-server"
)

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			library("shypl-tool-lang", "org.shypl.tool:tool-lang:1.0.0-SNAPSHOT")
			library("shypl-tool-logging", "org.shypl.tool:tool-logging:1.0.0-SNAPSHOT")
			library("shypl-tool-utils", "org.shypl.tool:tool-utils:1.0.0-SNAPSHOT")
			library("shypl-tool-io", "org.shypl.tool:tool-io:1.0.0-SNAPSHOT")
			
			library("logback", "ch.qos.logback:logback-classic:1.4.11")
		}
	}
}
