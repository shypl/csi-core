package org.shypl.csi.core

import org.shypl.tool.io.InputByteBuffer

interface Channel {
	val id: Any
	
	fun send(data: Byte)
	
	fun send(data: ByteArray)
	
	fun send(data: InputByteBuffer)
	
	fun close()
}