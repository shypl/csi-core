package org.shypl.csi.core.server._test

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.tool.io.InputByteBuffer

object EmptyInternalChannel : InternalChannel {
	override val id: Any = 0
	
	override fun useProcessor(processor: InternalChannelProcessor) {
	}
	
	override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {
	}
	
	override fun closeWithMarker(marker: Byte) {
	}
	
	override fun send(data: Byte) {
	}
	
	override fun send(data: ByteArray) {
	}
	
	override fun send(data: InputByteBuffer) {
		data.skipRead()
	}
	
	override fun close() {
	}
}