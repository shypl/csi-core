package org.shypl.csi.core.backend

import org.shypl.csi.core.internal.NothingChannel
import org.shypl.csi.core.internal.NothingConnectionProcessor
import org.shypl.csi.core.internal.NothingInternalConnection
import org.shypl.csi.core.backend._test.FakeTemporalAssistant
import org.shypl.csi.core.backend._test.TestConnectionAcceptor
import org.shypl.csi.core.backend.internal.AcceptationConnectionProcessor
import org.shypl.csi.core.backend.internal.RecoveryConnectionProcessor
import org.shypl.tool.io.ArrayByteBuffer
import kotlin.test.Test

class CoverageConnectionProcessors {
	
	@Test(expected = UnsupportedOperationException::class)
	fun `AcceptationConnectionProcessor processConnectionRecovery`() {
		AcceptationConnectionProcessor(
			FakeTemporalAssistant,
			TestConnectionAcceptor(),
			1,
			1
		).processConnectionRecovery(NothingChannel)
	}
	
	@Test(expected = UnsupportedOperationException::class)
	fun `RecoveryConnectionProcessor processConnectionAccept`() {
		RecoveryConnectionProcessor(
			NothingConnectionProcessor,
			NothingInternalConnection,
			FakeTemporalAssistant,
			1
		).processConnectionAccept(
			NothingChannel,
			NothingInternalConnection
		)
	}
	
	@Test(expected = UnsupportedOperationException::class)
	fun `RecoveryConnectionProcessor processChannelInput`() {
		RecoveryConnectionProcessor(
			NothingConnectionProcessor,
			NothingInternalConnection,
			FakeTemporalAssistant,
			1
		).processChannelInput(NothingChannel, ArrayByteBuffer(0))
	}
	
	@Test(expected = UnsupportedOperationException::class)
	fun `RecoveryConnectionProcessor processChannelClose`() {
		RecoveryConnectionProcessor(
			NothingConnectionProcessor,
			NothingInternalConnection,
			FakeTemporalAssistant,
			1
		).processChannelInterrupt(NothingInternalConnection)
	}
}