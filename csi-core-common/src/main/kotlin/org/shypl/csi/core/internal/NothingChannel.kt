package org.shypl.csi.core.internal

import org.shypl.csi.core.Channel
import org.shypl.tool.io.InputByteBuffer

object NothingChannel : Channel {
	override val id: Any
		get() = "nothing"
	
	override fun send(data: Byte) {
		throw UnsupportedOperationException()
	}
	
	override fun send(data: ByteArray) {
		throw UnsupportedOperationException()
	}
	
	override fun send(data: InputByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun close() {
		throw UnsupportedOperationException()
	}
}