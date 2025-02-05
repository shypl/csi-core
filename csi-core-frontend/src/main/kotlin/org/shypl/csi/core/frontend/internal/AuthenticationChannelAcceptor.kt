package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler
import org.shypl.csi.core.frontend.ChannelAcceptor
import org.shypl.csi.core.frontend.ChannelGate
import org.shypl.csi.core.frontend.ConnectFailReason
import org.shypl.csi.core.frontend.ConnectionAcceptor
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.putByteArray
import org.shypl.tool.io.putInt
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class AuthenticationChannelAcceptor(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val gate: ChannelGate,
	private val clientVersion: Int,
	private val authenticationKey: ByteArray,
	private val acceptor: ConnectionAcceptor,
	private val authenticationTimeoutSeconds: Int
) : ChannelAcceptor {
	
	override fun acceptChannel(channel: Channel): ChannelHandler {
		val clientChannel = ClientChannelImpl(
			channel,
			AuthenticationChannelProcessor(assistant, byteBuffers, gate, acceptor),
			assistant,
			byteBuffers,
			authenticationTimeoutSeconds
		)
		
		clientChannel.send(ByteArray(1 + 4 + 4 + authenticationKey.size).also {
			it[0] = ProtocolMarker.AUTHENTICATION
			it.putInt(1, clientVersion)
			it.putInt(1 + 4, authenticationKey.size)
			it.putByteArray(1 + 4 + 4, authenticationKey)
		})
		
		return clientChannel
	}
	
	override fun acceptFail() {
		acceptor.acceptFail(ConnectFailReason.REFUSED)
	}
}