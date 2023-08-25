package org.shypl.csi.core.client.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.client.ChannelGate
import org.shypl.csi.core.client.ConnectionHandler
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.Messages
import org.shypl.csi.core.internal.MessagingConnectionProcessor
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.lang.alsoOnFalse
import org.shypl.tool.lang.alsoOnTrue
import org.shypl.tool.logging.Logger
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class ClientMessagingConnectionProcessor(
	handler: ConnectionHandler,
	messages: Messages,
	logger: Logger,
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val activityTimeoutSeconds: Int,
	private val gate: ChannelGate,
	channel: Channel
) : MessagingConnectionProcessor<ConnectionHandler>(handler, messages, logger) {
	
	private var pinger = Cancelable.DUMMY
	
	init {
		startPinger(channel)
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		stopPinger()
		val recoveryHandler = handler.handleConnectionLost()
		return RecoveryConnectionProcessor(this, recoveryHandler, assistant, byteBuffers, gate, connection, activityTimeoutSeconds, lastIncomingMessageId)
	}
	
	override fun doProcessConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		startPinger(channel)
		return this
	}
	
	override fun doProcessConnectionClose(): ConnectionHandler {
		stopPinger()
		return NothingConnectionHandler()
	}
	
	override fun processChannelInputMarker(channel: Channel, buffer: InputByteBuffer, marker: Byte): Boolean {
		return when (marker) {
			ProtocolMarker.MESSAGING_PING          -> true
			ProtocolMarker.SERVER_CLOSE_SHUTDOWN   -> {
				channel.close()
				false
			}
			ProtocolMarker.SERVER_CLOSE_CONCURRENT -> {
				logger.warn("Closing because of the received marker ${ProtocolMarker.toString(marker)}")
				channel.close()
				false
			}
			ProtocolMarker.SERVER_SHUTDOWN_TIMEOUT -> {
				buffer.isReadable(4)
					.alsoOnTrue {
						val shutdownTimeoutSeconds = buffer.readInt()
						handler.handleConnectionCloseTimeout(shutdownTimeoutSeconds)
					}
					.alsoOnFalse {
						buffer.backRead(1)
					}
			}
			
			else                                   -> super.processChannelInputMarker(channel, buffer, marker)
		}
	}
	
	private fun startPinger(channel: Channel) {
		stopPinger()
		pinger = assistant.repeat(activityTimeoutSeconds * 1000) {
			channel.send(ProtocolMarker.MESSAGING_PING)
		}
	}
	
	private fun stopPinger() {
		pinger.cancel()
		pinger = Cancelable.DUMMY
	}
}
