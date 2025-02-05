package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.frontend.ConnectionHandler
import org.shypl.csi.core.frontend.ConnectionRecoveryHandler
import org.shypl.tool.io.InputByteBuffer

internal class NothingConnectionHandler : ConnectionHandler {
	override fun handleConnectionMessage(message: InputByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun handleConnectionLost(): ConnectionRecoveryHandler {
		throw UnsupportedOperationException()
	}
	
	override fun handleConnectionCloseTimeout(seconds: Int) {
		throw UnsupportedOperationException()
	}
	
	override fun handleConnectionClose() {
		throw UnsupportedOperationException()
	}
}