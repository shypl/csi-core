package org.shypl.csi.core.client.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.InputByteBuffer

internal class RecoveryChannelProcessor(
	private val connection: InternalConnection
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		if (buffer.readByte() == ProtocolMarker.RECOVERY) {
			if (buffer.isReadable(4)) {
				
				val messageId = buffer.readInt()
				connection.recovery(channel, messageId)
				
				return ChannelProcessorInputResult.CONTINUE
			}
		}
		else {
			channel.close()
		}
		
		return ChannelProcessorInputResult.BREAK
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {
		connection.close()
	}
}