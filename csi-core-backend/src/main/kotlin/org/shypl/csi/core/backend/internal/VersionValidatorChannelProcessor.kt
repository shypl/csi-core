package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.InputByteBuffer

internal class VersionValidatorChannelProcessor(
	private val serverVersion: Int,
	private val authentication: InternalChannelProcessor
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		if (buffer.isReadable(4)) {
			val clientVersion = buffer.readInt()
			
			if (clientVersion < serverVersion) {
				channel.closeWithMarker(ProtocolMarker.SERVER_CLOSE_VERSION)
				return ChannelProcessorInputResult.BREAK
			}
			channel.useProcessor(authentication)
			return ChannelProcessorInputResult.CONTINUE
		}
		
		return ChannelProcessorInputResult.BREAK
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {}
}