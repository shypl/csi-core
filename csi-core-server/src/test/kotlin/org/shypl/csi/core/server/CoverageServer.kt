package org.shypl.csi.core.server

import org.junit.Test
import org.shypl.csi.core.server._test.Errors
import org.shypl.csi.core.server._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.server._test.TestChannelGate
import org.shypl.csi.core.server._test.TestConnectionAcceptor
import org.shypl.csi.core.server._test.TestConnectionAuthenticator
import org.shypl.csi.core.server._test.assistant

class CoverageServer {
	@Test
	fun `Coverage default values in constructor`() {
		Server(
			assistant(2, "server"),
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAuthenticator(),
			TestConnectionAcceptor(),
			TestChannelGate(Errors()),
			1,
			1
		).stop()
	}
}