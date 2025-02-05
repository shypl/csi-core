package org.shypl.csi.core.server

import org.shypl.csi.core.internal.Messages
import org.shypl.csi.core.internal.NothingChannel
import org.shypl.csi.core.internal.NothingInternalConnection
import org.shypl.csi.core.server._test.FakeTemporalAssistant
import org.shypl.csi.core.server._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.server.internal.NothingConnectionHandler
import org.shypl.csi.core.server.internal.ServerMessagingConnectionProcessor
import org.shypl.tool.logging.ownLogger
import kotlin.test.Test

class CoverageServerMessagingConnectionProcessor {
	
	@Test(UnsupportedOperationException::class)
	fun `Unsupported processConnectionAccept`() {
		ServerMessagingConnectionProcessor(NothingConnectionHandler, Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, FakeTemporalAssistant, 1)
			.processConnectionAccept(
				NothingChannel,
				NothingInternalConnection
			)
	}
}