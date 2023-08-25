package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer

interface InternalConnectionProcessor {
	fun processConnectionAccept(channel: org.shypl.csi.core.Channel, connection: InternalConnection): InternalConnectionProcessor
	
	fun processConnectionRecovery(channel: org.shypl.csi.core.Channel): InternalConnectionProcessor
	
	fun processConnectionClose()
	
	fun processChannelInput(channel: org.shypl.csi.core.Channel, buffer: InputByteBuffer): Boolean
	
	fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor
}