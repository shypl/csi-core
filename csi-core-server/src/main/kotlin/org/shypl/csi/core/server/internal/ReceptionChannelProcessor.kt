package org.shypl.csi.core.server.internal

import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.InputByteBuffer

internal class ReceptionChannelProcessor(
	private val authorization: InternalChannelProcessor,
	private val recovery: InternalChannelProcessor
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		return when (val marker = buffer.readByte()) {
			ProtocolMarker.AUTHORIZATION -> {
				channel.useProcessor(authorization)
				ChannelProcessorInputResult.CONTINUE
			}
			ProtocolMarker.RECOVERY      -> {
				channel.useProcessor(recovery)
				ChannelProcessorInputResult.CONTINUE
			}
			ProtocolMarker.CLOSE_DEFINITELY,
			ProtocolMarker.CLOSE_PROTOCOL_BROKEN,
			ProtocolMarker.CLOSE_ERROR   -> {
				channel.close()
				ChannelProcessorInputResult.BREAK
			}
			else                         -> throw ProtocolBrokenException("Unknown marker ${ProtocolMarker.toString(marker)}")
		}
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {}
}

