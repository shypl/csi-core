package org.shypl.csi.core.client.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.client.ChannelGate
import org.shypl.csi.core.client.ConnectionRecoveryHandler
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.NothingConnectionProcessor
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class RecoveryConnectionProcessor(
	private var messagingProcessor: InternalConnectionProcessor,
	private var recoveryHandler: ConnectionRecoveryHandler,
	assistant: TemporalAssistant,
	byteBuffers: ObjectPool<ByteBuffer>,
	gate: ChannelGate,
	connection: InternalConnection,
	activityTimeoutSeconds: Int,
	lastIncomingMessageId: Int
) : InternalConnectionProcessor {
	
	init {
		assistant.schedule(10) {
			gate.openChannel(RecoveryChannelAcceptor(assistant, byteBuffers, connection, activityTimeoutSeconds, lastIncomingMessageId))
		}
	}
	
	override fun processConnectionAccept(channel: Channel, connection: InternalConnection): InternalConnectionProcessor {
		throw UnsupportedOperationException()
	}
	
	override fun processConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		var p = messagingProcessor
		val h = recoveryHandler
		
		free()
		
		p = p.processConnectionRecovery(channel)
		h.handleConnectionRecovered()
		
		return p
	}
	
	override fun processConnectionClose() {
		with(messagingProcessor) {
			free()
			processConnectionClose()
		}
	}
	
	override fun processChannelInput(channel: Channel, buffer: InputByteBuffer): Boolean {
		throw UnsupportedOperationException()
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		throw UnsupportedOperationException()
	}
	
	private fun free() {
		messagingProcessor = NothingConnectionProcessor
	}
}
