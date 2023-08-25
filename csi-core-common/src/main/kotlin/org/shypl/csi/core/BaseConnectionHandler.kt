package org.shypl.csi.core

import org.shypl.tool.io.InputByteBuffer

interface BaseConnectionHandler {
	fun handleConnectionMessage(message: InputByteBuffer)
	
	fun handleConnectionClose()
}