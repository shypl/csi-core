package org.shypl.csi.core.server.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.csi.core.server.ConnectionAcceptor
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.io.putInt
import org.shypl.tool.io.putLong
import org.shypl.tool.utils.assistant.TemporalAssistant

internal class AcceptationConnectionProcessor<I : Any>(
	private val assistant: TemporalAssistant,
	private val connectionAcceptor: ConnectionAcceptor<I>,
	private val activityTimeoutSeconds: Int,
	private val identity: I
) : InternalConnectionProcessor {
	
	override fun processConnectionAccept(channel: Channel, connection: InternalConnection): InternalConnectionProcessor {
		
		channel.send(ByteArray(1 + 8 + 4).apply {
			set(0, ProtocolMarker.AUTHORIZATION)
			putLong(1, connection.id)
			putInt(1 + 8, activityTimeoutSeconds)
		})
		
		val handler = connectionAcceptor.acceptConnection(identity, connection)
		
		return ServerMessagingConnectionProcessor(handler, connection.messages, connection.logger, assistant, activityTimeoutSeconds)
	}
	
	override fun processConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		throw UnsupportedOperationException()
	}
	
	override fun processConnectionClose() {}
	
	override fun processChannelInput(channel: Channel, buffer: InputByteBuffer): Boolean {
		throw ProtocolBrokenException("Not expected incoming data")
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		connection.close()
		return this
	}
}
