package org.shypl.csi.core.backend._test

import org.shypl.csi.core.Channel
import org.shypl.tool.io.InputByteBuffer

object EmptyChannel : Channel {
	override val id: Any = 0
	
	override fun send(data: Byte) {}
	
	override fun send(data: ByteArray) {}
	
	override fun send(data: InputByteBuffer) {
		data.skipRead()
	}
	
	override fun close() {}
}

