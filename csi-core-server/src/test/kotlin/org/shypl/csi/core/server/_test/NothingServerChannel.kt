package org.shypl.csi.core.server._test

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.server.internal.ServerChannel
import org.shypl.tool.io.InputByteBuffer

object NothingServerChannel : ServerChannel {
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
	
	override fun closeWithMarker(marker: Byte) {
		throw UnsupportedOperationException()
	}
	
	override fun useProcessor(processor: InternalChannelProcessor) {
		throw UnsupportedOperationException()
	}
	
	override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {
		throw UnsupportedOperationException()
	}
}