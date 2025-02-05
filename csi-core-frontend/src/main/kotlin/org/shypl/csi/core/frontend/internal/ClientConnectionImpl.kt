package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalConnectionImpl
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.lang.toHexString
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

internal class ClientConnectionImpl(
	id: Long,
	channel: InternalChannel,
	processor: InternalConnectionProcessor,
	assistant: TemporalAssistant,
	byteBufferPool: ObjectPool<ByteBuffer>
) : InternalConnectionImpl(id, channel, processor, assistant, byteBufferPool, id.toHexString()), ClientConnection {
	override fun syncProcessClose() {}
}