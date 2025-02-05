package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannelImpl
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class ClientChannelImpl(
	channel: Channel,
	processor: InternalChannelProcessor,
	assistant: TemporalAssistant,
	byteBuffers: ObjectPool<ByteBuffer>,
	activityTimeoutSeconds: Int
) : InternalChannelImpl(channel, processor, byteBuffers, assistant, activityTimeoutSeconds), ClientChannel {
	
	override fun processClose() {
	}
}