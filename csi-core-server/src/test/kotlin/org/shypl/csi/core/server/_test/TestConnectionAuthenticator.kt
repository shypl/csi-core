package org.shypl.csi.core.server._test

import org.shypl.csi.core.server.ConnectionAuthenticator
import org.shypl.tool.io.getInt

class TestConnectionAuthenticator : ConnectionAuthenticator<Int> {
	override fun authenticateConnection(data: ByteArray): Int? {
		val clientId = data.getInt(0)
		return if (clientId == 0) null else clientId
	}
}