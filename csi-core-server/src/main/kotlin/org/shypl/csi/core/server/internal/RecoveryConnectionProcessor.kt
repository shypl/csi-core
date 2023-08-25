package org.shypl.csi.core.server.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.Connection
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.NothingConnectionProcessor
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant

internal class RecoveryConnectionProcessor(
	private var messagingProcessor: InternalConnectionProcessor,
	connection: Connection,
	assistant: TemporalAssistant,
	activityTimeoutSeconds: Int
) : InternalConnectionProcessor {
	
	private var timeout = assistant.schedule(activityTimeoutSeconds * 2 * 1000) {
		connection.close()
	}
	
	override fun processConnectionAccept(channel: Channel, connection: InternalConnection): InternalConnectionProcessor {
		throw UnsupportedOperationException()
	}
	
	override fun processConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		return with(messagingProcessor) {
			free()
			processConnectionRecovery(channel)
		}
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
		timeout.cancel()
		timeout = Cancelable.DUMMY
	}
}