package org.shypl.csi.core.client.internal

import org.shypl.csi.core.client.ChannelGate
import org.shypl.csi.core.client.ConnectFailReason
import org.shypl.csi.core.client.ConnectionAcceptor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class AuthenticationChannelProcessor(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val gate: ChannelGate,
	private var acceptor: ConnectionAcceptor
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		val marker = buffer.readByte()
		if (marker == ProtocolMarker.AUTHENTICATION) {
			if (buffer.isReadable(8 + 4)) {
				val connectionId = buffer.readLong()
				val activityTimeoutSeconds = buffer.readInt()
				val connection = ClientConnectionImpl(
					connectionId,
					channel,
					AuthenticationConnectionProcessor(assistant, byteBuffers, activityTimeoutSeconds, acceptor, gate),
					assistant,
					byteBuffers
				)
				acceptor = NothingConnectionAcceptor()
				channel.useProcessor(connection, activityTimeoutSeconds * 2)
				connection.accept()
				return ChannelProcessorInputResult.CONTINUE
			}
			
			buffer.backRead(1)
		}
		else when (marker) {
			ProtocolMarker.SERVER_CLOSE_VERSION        -> fail(channel, ConnectFailReason.VERSION)
			ProtocolMarker.SERVER_CLOSE_AUTHENTICATION -> fail(channel, ConnectFailReason.AUTHENTICATION)
			ProtocolMarker.SERVER_CLOSE_SHUTDOWN       -> fail(channel, ConnectFailReason.REFUSED)
			ProtocolMarker.CLOSE_DEFINITELY            -> fail(channel, ConnectFailReason.REFUSED)
			ProtocolMarker.CLOSE_ERROR                 -> fail(channel, ConnectFailReason.ERROR)
			ProtocolMarker.CLOSE_PROTOCOL_BROKEN       -> fail(channel, ConnectFailReason.ERROR)
			ProtocolMarker.SERVER_CLOSE_CONCURRENT     -> fail(channel, ConnectFailReason.REFUSED)
			ProtocolMarker.SERVER_SHUTDOWN_TIMEOUT     -> fail(channel, ConnectFailReason.REFUSED, ProtocolMarker.CLOSE_DEFINITELY)
			else                                       -> fail(channel, ConnectFailReason.ERROR, ProtocolMarker.CLOSE_PROTOCOL_BROKEN)
		}
		return ChannelProcessorInputResult.BREAK
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {
		if (interrupted) {
			fail(ConnectFailReason.REFUSED)
		}
	}
	
	private fun fail(reason: ConnectFailReason) {
		with(acceptor) {
			acceptor = NothingConnectionAcceptor()
			acceptFail(reason)
		}
	}
	
	private fun fail(channel: InternalChannel, reason: ConnectFailReason) {
		fail(reason)
		channel.close()
	}
	
	private fun fail(channel: InternalChannel, reason: ConnectFailReason, marker: Byte) {
		fail(reason)
		channel.closeWithMarker(marker)
	}
}
