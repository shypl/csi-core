package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannelImpl
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

class ServerChannelImpl(
	channel: Channel,
	processor: InternalChannelProcessor,
	byteBuffers: ObjectPool<ByteBuffer>,
	assistant: TemporalAssistant,
	activityTimeoutSeconds: Int,
	private var releaser: ServerChannelReleaser
) : InternalChannelImpl(channel, processor, byteBuffers, assistant, activityTimeoutSeconds), ServerChannel {
	
	override fun processClose() {
		releaser.releaseServerChannel(this)
		releaser = NothingServerChannelReleaser
	}
}
