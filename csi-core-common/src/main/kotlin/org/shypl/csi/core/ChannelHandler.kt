package org.shypl.csi.core

import org.shypl.tool.io.InputByteBuffer

interface ChannelHandler {
	fun handleChannelInput(data: InputByteBuffer)
	
	fun handleChannelClose()
}