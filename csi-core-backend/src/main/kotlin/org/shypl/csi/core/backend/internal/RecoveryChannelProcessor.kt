package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.csi.core.internal.TransitionChannelProcessor
import org.shypl.tool.io.InputByteBuffer

internal class RecoveryChannelProcessor(
	private val acceptor: ConnectionRecoveryAcceptor
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		return if (buffer.isReadable(8 + 4)) {
			val connectionId = buffer.readLong()
			val lastSentMessageId = buffer.readInt()
			
			val connection = acceptor.acceptRecovery(connectionId)
			
			if (connection == null) {
				channel.closeWithMarker(ProtocolMarker.SERVER_CLOSE_RECOVERY_FAIL)
				ChannelProcessorInputResult.BREAK
			}
			else {
				channel.useProcessor(TransitionChannelProcessor)
				connection.recovery(channel, lastSentMessageId)
				ChannelProcessorInputResult.CONTINUE
			}
		}
		else {
			ChannelProcessorInputResult.BREAK
		}
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {}
}

