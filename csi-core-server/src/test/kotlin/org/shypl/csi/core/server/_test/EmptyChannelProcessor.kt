package org.shypl.csi.core.server._test

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.tool.io.InputByteBuffer

object EmptyChannelProcessor : InternalChannelProcessor {
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		buffer.skipRead()
		return ChannelProcessorInputResult.CONTINUE
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {}
}