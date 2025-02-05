package org.shypl.csi.core.backend

import org.junit.Test
import org.shypl.csi.core.internal.NothingInternalChannel
import org.shypl.csi.core.backend._test.EmptyConnectionProcessor
import org.shypl.csi.core.backend._test.EmptyInternalChannel
import org.shypl.csi.core.backend._test.EmptyServerConnectionReleaser
import org.shypl.csi.core.backend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.backend._test.assistant
import org.shypl.csi.core.backend.internal.ServerConnectionImpl
import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.lang.waitWhile

class CoverageServerConnectionImpl {
	@Test
	fun `Recovery on closed connection`() {
		val connection = ServerConnectionImpl(
			1,
			1,
			EmptyInternalChannel,
			EmptyConnectionProcessor,
			assistant(),
			GLOBAL_BYTE_BUFFER_POOL,
			EmptyServerConnectionReleaser
		)
		
		var b = true
		connection.close { b = false }
		
		waitWhile(1000) { b }
		
		connection.recovery(EmptyInternalChannel, 0)
	}
	
	@Test
	fun `Close on closed connection`() {
		val connection = ServerConnectionImpl(
			1,
			1,
			EmptyInternalChannel,
			EmptyConnectionProcessor,
			assistant(),
			GLOBAL_BYTE_BUFFER_POOL,
			EmptyServerConnectionReleaser
		)
		
		connection.close {
			connection.close()
		}
	}
	
	@Test
	fun `Process close on outside close`() {
		val connection = ServerConnectionImpl(
			1,
			1,
			EmptyInternalChannel,
			EmptyConnectionProcessor,
			assistant(),
			GLOBAL_BYTE_BUFFER_POOL,
			EmptyServerConnectionReleaser
		)
		
		connection.close()
		connection.processChannelClose(EmptyInternalChannel, true)
	}
	
	@Test
	fun `Process input with wrong channel`() {
		val connection = ServerConnectionImpl(
			1,
			1,
			EmptyInternalChannel,
			EmptyConnectionProcessor,
			assistant(),
			GLOBAL_BYTE_BUFFER_POOL,
			EmptyServerConnectionReleaser
		)
		
		connection.processChannelInput(NothingInternalChannel, ArrayByteBuffer(0))
	}
}

