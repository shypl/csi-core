package org.shypl.csi.core

import org.shypl.tool.io.InputByteBuffer

interface Connection {
	val id: Long
	val loggingName: String
	
	fun sendMessage(data: Byte)
	
	fun sendMessage(data: ByteArray)
	
	fun sendMessage(data: InputByteBuffer)
	
	fun close()
	
	fun close(handler: () -> Unit)
	
	fun closeDueError()
}