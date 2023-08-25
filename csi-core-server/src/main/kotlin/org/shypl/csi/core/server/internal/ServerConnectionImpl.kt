package org.shypl.csi.core.server.internal

import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalConnectionImpl
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.lang.toHexString
import org.shypl.tool.lang.waitWhile
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class ServerConnectionImpl<I : Any>(
	id: Long,
	override val identity: I,
	channel: InternalChannel,
	processor: InternalConnectionProcessor,
	assistant: TemporalAssistant,
	byteBuffers: ObjectPool<ByteBuffer>,
	private val releaser: ServerConnectionReleaser<I>,
) : InternalConnectionImpl(
	id,
	channel,
	processor,
	assistant,
	byteBuffers,
	"$identity-${id.toHexString()}"
), ServerConnection<I> {
	
	override fun tryRecovery(channel: InternalChannel, lastSentMessageId: Int): Boolean {
		return !waitWhile(1000, 10) {
			!super.tryRecovery(channel, lastSentMessageId)
		}
	}
	
	override fun syncProcessClose() {
		releaser.releaseServerConnection(this)
	}
}