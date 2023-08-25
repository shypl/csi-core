package org.shypl.csi.core.internal

import org.shypl.csi.core.Connection
import org.shypl.tool.logging.Logger

interface InternalConnection : Connection {
	val logger: Logger
	val messages: Messages
	
	fun accept()
	
	fun recovery(channel: InternalChannel, lastSentMessageId: Int)
	
	fun closeWithMarker(marker: Byte)
	
	fun closeWithMarker(marker: Byte, handler: () -> Unit)
}