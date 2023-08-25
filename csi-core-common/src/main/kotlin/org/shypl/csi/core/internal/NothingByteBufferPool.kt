package org.shypl.csi.core.internal

import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.utils.pool.ObjectPool

object NothingByteBufferPool : ObjectPool<ByteBuffer> {
	override fun take(): ByteBuffer {
		throw UnsupportedOperationException()
	}
	
	override fun back(instance: ByteBuffer) {
		throw UnsupportedOperationException()
	}
	
	override fun clear() {
		throw UnsupportedOperationException()
	}
}