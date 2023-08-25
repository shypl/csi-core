package org.shypl.csi.core.internal

import org.shypl.csi.core.Connection
import org.shypl.tool.io.InputByteBuffer

object NothingConnection : Connection {
	override val id: Long
		get() = 0
	
	override val loggingName: String
		get() = "Nothing"
	
	override fun sendMessage(data: Byte) {
		throw UnsupportedOperationException()
	}
	
	override fun sendMessage(data: ByteArray) {
		throw UnsupportedOperationException()
	}
	
	override fun sendMessage(data: InputByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun close() {
		throw UnsupportedOperationException()
	}
	
	override fun close(handler: () -> Unit) {
		throw UnsupportedOperationException()
	}
	
	override fun closeDueError() {
		throw UnsupportedOperationException()
	}
}