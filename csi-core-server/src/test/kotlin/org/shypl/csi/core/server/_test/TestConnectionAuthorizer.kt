package org.shypl.csi.core.server._test

import org.shypl.csi.core.server.ConnectionAuthorizer
import org.shypl.tool.io.getInt

class TestConnectionAuthorizer : ConnectionAuthorizer<Int> {
	override fun authorizeConnection(data: ByteArray): Int? {
		val clientId = data.getInt(0)
		return if (clientId == 0) null else clientId
	}
}