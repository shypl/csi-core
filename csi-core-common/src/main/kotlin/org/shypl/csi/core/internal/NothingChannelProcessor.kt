package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer

object NothingChannelProcessor : InternalChannelProcessor {
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		throw UnsupportedOperationException()
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {
		throw UnsupportedOperationException()
	}
}