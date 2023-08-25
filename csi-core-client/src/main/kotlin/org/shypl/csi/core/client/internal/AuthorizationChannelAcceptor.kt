package org.shypl.csi.core.client.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler
import org.shypl.csi.core.client.ChannelAcceptor
import org.shypl.csi.core.client.ChannelGate
import org.shypl.csi.core.client.ConnectFailReason
import org.shypl.csi.core.client.ConnectionAcceptor
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.putByteArray
import org.shypl.tool.io.putInt
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class AuthorizationChannelAcceptor(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val gate: ChannelGate,
	private val clientVersion: Int,
	private val authorizationKey: ByteArray,
	private val acceptor: ConnectionAcceptor,
	private val authorizationTimeoutSeconds: Int
) : ChannelAcceptor {
	
	override fun acceptChannel(channel: Channel): ChannelHandler {
		val clientChannel = ClientChannelImpl(
			channel,
			AuthorizationChannelProcessor(assistant, byteBuffers, gate, acceptor),
			assistant,
			byteBuffers,
			authorizationTimeoutSeconds
		)
		
		clientChannel.send(ByteArray(1 + 4 + 4 + authorizationKey.size).also {
			it[0] = ProtocolMarker.AUTHORIZATION
			it.putInt(1, clientVersion)
			it.putInt(1 + 4, authorizationKey.size)
			it.putByteArray(1 + 4 + 4, authorizationKey)
		})
		
		return clientChannel
	}
	
	override fun acceptFail() {
		acceptor.acceptFail(ConnectFailReason.REFUSED)
	}
}