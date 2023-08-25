package org.shypl.csi.core.internal

import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.tool.io.InputByteBuffer

object TransitionChannelProcessor : InternalChannelProcessor {
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		throw ProtocolBrokenException("Not expected incoming data")
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {
	}
}