package org.shypl.csi.core.backend

import org.junit.Test
import org.shypl.csi.core.backend._test.Errors
import org.shypl.csi.core.backend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.backend._test.TestChannelGate
import org.shypl.csi.core.backend._test.TestConnectionAcceptor
import org.shypl.csi.core.backend._test.TestConnectionAuthenticator
import org.shypl.csi.core.backend._test.assistant

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