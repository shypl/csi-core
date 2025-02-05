package org.shypl.csi.core.backend._test

import org.shypl.csi.core.Channel
import org.shypl.csi.core.internal.InternalConnectionProcessor
import org.shypl.csi.core.internal.InternalConnection
import org.shypl.csi.core.internal.NothingConnectionProcessor
import org.shypl.tool.io.InputByteBuffer

object EmptyConnectionProcessor : InternalConnectionProcessor {
	override fun processConnectionAccept(channel: Channel, connection: InternalConnection): InternalConnectionProcessor {
		return NothingConnectionProcessor
	}
	
	override fun processConnectionRecovery(channel: Channel): InternalConnectionProcessor {
		return NothingConnectionProcessor
	}
	
	override fun processConnectionClose() {
	}
	
	override fun processChannelInput(channel: Channel, buffer: InputByteBuffer): Boolean {
		return false
	}
	
	override fun processChannelInterrupt(connection: InternalConnection): InternalConnectionProcessor {
		return NothingConnectionProcessor
	}
}