package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.csi.core.frontend.ChannelGate
import org.shypl.csi.core.frontend.ConnectionAcceptor
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class AuthenticationConnectionProcessor(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val activityTimeoutSeconds: Int,
	private val acceptor: ConnectionAcceptor,
	private val gate: ChannelGate
) : InternalConnectionProcessor {
	override fun processConnectionAccept(channel: Channel, connection: InternalConnection): InternalConnectionProcessor {
		val handler = acceptor.acceptConnection(connection)
		return ClientMessagingConnectionProcessor(handler, connection.messages, connection.logger, assistant, byteBuffers, activityTimeoutSeconds, gate, channel)
	}
	
	override fun processConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		throw UnsupportedOperationException()
	}
	
	override fun processChannelInput(channel: Channel, buffer: InputByteBuffer): Boolean {
		throw ProtocolBrokenException("Not expected incoming data")
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		connection.close()
		return this
	}
	
	override fun processConnectionClose() {}
}