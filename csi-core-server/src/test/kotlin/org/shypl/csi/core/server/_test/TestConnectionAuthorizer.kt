package org.shypl.csi.core.server._test

import org.shypl.csi.core.server.ConnectionAuthorizer
import org.shypl.tool.io.getInt

class TestConnectionAuthorizer : ConnectionAuthorizer<Int> {
	override fun authorizeConnection(key: ByteArray): Int? {
		val clientId = key.getInt(0)
		return if (clientId == 0) null else clientId
	}
}