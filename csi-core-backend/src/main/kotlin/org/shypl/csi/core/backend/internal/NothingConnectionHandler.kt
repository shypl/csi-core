package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.backend.ConnectionHandler
import org.shypl.tool.io.InputByteBuffer

internal object NothingConnectionHandler : ConnectionHandler {
	override fun handleConnectionMessage(message: InputByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun handleConnectionClose() {
		throw UnsupportedOperationException()
	}
}
