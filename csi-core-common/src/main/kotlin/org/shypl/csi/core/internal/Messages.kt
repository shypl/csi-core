package org.shypl.csi.core.internal

import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.utils.pool.ObjectPool

class Messages(byteBuffers: ObjectPool<ByteBuffer>) {
	val incoming = LastIncomingMessageId()
	val outgoing = OutgoingMessageBuffer(byteBuffers)
}
