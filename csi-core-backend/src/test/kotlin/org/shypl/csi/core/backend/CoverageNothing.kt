package org.shypl.csi.core.backend

import org.junit.Test
import org.shypl.csi.core.backend._test.NothingServerChannel
import org.shypl.csi.core.backend.internal.NothingConnectionHandler
import org.shypl.csi.core.backend.internal.NothingServerChannelReleaser
import org.shypl.tool.io.ArrayByteBuffer

class CoverageNothing {
	@Test(expected = UnsupportedOperationException::class)
	fun `NothingConnectionHandler handleConnectionMessage`() {
		NothingConnectionHandler.handleConnectionMessage(ArrayByteBuffer(0))
	}
	
	@Test(expected = UnsupportedOperationException::class)
	fun `NothingConnectionHandler handleConnectionClose`() {
		NothingConnectionHandler.handleConnectionClose()
	}
	
	@Test(expected = UnsupportedOperationException::class)
	fun `NothingServerChannelReleaser handleConnectionClose`() {
		NothingServerChannelReleaser.releaseServerChannel(NothingServerChannel)
	}
}