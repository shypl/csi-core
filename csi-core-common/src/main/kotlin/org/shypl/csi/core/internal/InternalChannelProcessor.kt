package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer

interface InternalChannelProcessor {
	fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult
	
	fun processChannelClose(channel: InternalChannel, interrupted: Boolean)
}