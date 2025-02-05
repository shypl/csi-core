package org.shypl.csi.core.frontend._test

import org.shypl.csi.core.Channel
import org.shypl.csi.core.frontend.ChannelAcceptor
import org.shypl.csi.core.frontend.ChannelGate
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.io.InputByteBuffer

val GLOBAL_ASSISTANT = assistant(8, "global")

inline fun gate(crossinline code: ChannelAcceptor.() -> Unit): ChannelGate {
	return object : ChannelGate {
		override fun openChannel(acceptor: ChannelAcceptor) {
			GLOBAL_ASSISTANT.execute {
				acceptor.code()
			}
		}
	}
}

open class FnChannel(
	private val send: InputByteBuffer.() -> Unit = { skipRead() },
	private val close: () -> Unit = {},
	override val id: Any = 1,
) : Channel {
	
	override fun send(data: Byte) {
		send(ArrayByteBuffer { writeByte(data) })
	}
	
	override fun send(data: ByteArray) {
		send(ArrayByteBuffer { writeArray(data) })
	}
	
	override fun send(data: InputByteBuffer) {
		send.invoke(data)
	}
	
	override fun close() {
		close.invoke()
	}
	
}

open class FnInternalChannel(
	send: InputByteBuffer.() -> Unit = { skipRead() },
	close: () -> Unit = {},
	id: Any = 1,
) : FnChannel(send, close, id), InternalChannel {
	
	private var processor: InternalChannelProcessor? = null
	
	override fun useProcessor(processor: InternalChannelProcessor) {
		this.processor = processor
	}
	
	override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {
		this.processor = processor
	}
	
	override fun closeWithMarker(marker: Byte) {
		send(marker)
		close()
	}
	
	override fun close() {
		super.close()
		processor?.processChannelClose(this, false)
	}
}