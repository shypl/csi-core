package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler
import org.shypl.csi.core.frontend.ChannelAcceptor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.putInt
import org.shypl.tool.io.putLong
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class RecoveryChannelAcceptor(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val connection: InternalConnection,
	private val activityTimeoutSeconds: Int,
	private val lastIncomingMessageId: Int
) : ChannelAcceptor {
	
	override fun acceptChannel(channel: Channel): ChannelHandler {
		val clientChannel = ClientChannelImpl(
			channel,
			RecoveryChannelProcessor(connection),
			assistant,
			byteBuffers,
			activityTimeoutSeconds
		)
		
		clientChannel.send(ByteArray(1 + 8 + 4).also {
			it[0] = ProtocolMarker.RECOVERY
			it.putLong(1, connection.id)
			it.putInt(1 + 8, lastIncomingMessageId)
		})
		
		return clientChannel
	}
	
	override fun acceptFail() {
		connection.close()
	}
}