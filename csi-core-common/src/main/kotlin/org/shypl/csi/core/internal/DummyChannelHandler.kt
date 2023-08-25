package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer

object DummyChannelHandler : org.shypl.csi.core.ChannelHandler {
	override fun handleChannelInput(data: InputByteBuffer) {
		data.skipRead()
	}
	
	override fun handleChannelClose() {}
}
