package org.shypl.csi.core.server

import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.csi.core.internal.DummyChannelHandler
import org.shypl.csi.core.internal.NothingChannelProcessor
import org.shypl.csi.core.internal.NothingInternalChannel
import org.shypl.csi.core.internal.TransitionChannelProcessor
import org.shypl.tool.io.ArrayByteBuffer
import kotlin.test.Test

class Coverage {
	@Test
	fun `DummyChannelHandler full`() {
		DummyChannelHandler.handleChannelClose()
		DummyChannelHandler.handleChannelInput(ArrayByteBuffer())
	}
	
	@Test(ProtocolBrokenException::class)
	fun `TransitionChannelProcessor processChannelInput`() {
		TransitionChannelProcessor.processChannelInput(NothingInternalChannel, ArrayByteBuffer(0))
	}
	
	@Test(UnsupportedOperationException::class)
	fun `NothingChannelProcessor processChannelInput`() {
		NothingChannelProcessor.processChannelInput(NothingInternalChannel, ArrayByteBuffer(0))
	}
	
	@Test(UnsupportedOperationException::class)
	fun `NothingChannelProcessor processChannelClose`() {
		NothingChannelProcessor.processChannelClose(NothingInternalChannel, false)
	}
}