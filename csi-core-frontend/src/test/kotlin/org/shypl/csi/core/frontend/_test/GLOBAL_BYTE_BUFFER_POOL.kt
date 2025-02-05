package org.shypl.csi.core.frontend._test

import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.utils.pool.ArrayObjectPool
import org.shypl.tool.utils.pool.ObjectAllocator

val GLOBAL_BYTE_BUFFER_POOL = ArrayObjectPool(64, object : ObjectAllocator<ByteBuffer> {
	override fun produceInstance(): ByteBuffer {
		return ArrayByteBuffer()
	}
	
	override fun clearInstance(instance: ByteBuffer) {
		instance.clear()
	}
	
	override fun disposeInstance(instance: ByteBuffer) {
		instance.clear()
	}
})