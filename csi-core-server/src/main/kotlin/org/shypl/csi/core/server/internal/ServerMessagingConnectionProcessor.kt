package org.shypl.csi.core.server.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.Messages
import org.shypl.csi.core.internal.MessagingConnectionProcessor
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.csi.core.server.ConnectionHandler
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.io.putInt
import org.shypl.tool.logging.Logger
import org.shypl.tool.utils.assistant.TemporalAssistant

internal class ServerMessagingConnectionProcessor(
	handler: ConnectionHandler,
	messages: Messages,
	logger: Logger,
	private val assistant: TemporalAssistant,
	private val activityTimeoutSeconds: Int
) : MessagingConnectionProcessor<ConnectionHandler>(handler, messages, logger) {
	
	override fun doProcessConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		channel.send(ByteArray(1 + 4).apply {
			set(0, ProtocolMarker.RECOVERY)
			putInt(1, lastIncomingMessageId)
		})
		return this
	}
	
	override fun doProcessConnectionClose(): ConnectionHandler {
		return NothingConnectionHandler
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		return RecoveryConnectionProcessor(this, connection, assistant, activityTimeoutSeconds)
	}
	
	override fun processChannelInputMarker(channel: Channel, buffer: InputByteBuffer, marker: Byte): Boolean {
		if (marker == ProtocolMarker.MESSAGING_PING) {
			channel.send(ProtocolMarker.MESSAGING_PING)
			return true
		}
		return super.processChannelInputMarker(channel, buffer, marker)
	}
}
