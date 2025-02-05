package org.shypl.csi.core.backend

import org.junit.Test
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.backend._test.FakeTemporalAssistant
import org.shypl.csi.core.backend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.backend._test.TestConnectionAuthenticator
import org.shypl.csi.core.backend.internal.Channels
import org.shypl.csi.core.backend.internal.ConnectionAuthenticationAcceptor
import org.shypl.csi.core.backend.internal.ConnectionRecoveryAcceptor
import org.shypl.csi.core.backend.internal.ServerConnection
import org.shypl.tool.utils.Sluice

class CoverageChannels {
	@Test(expected = IllegalArgumentException::class)
	fun `Coverage bad activityTimeoutSeconds`() {
		Channels(
			Sluice(false),
			GLOBAL_BYTE_BUFFER_POOL,
			FakeTemporalAssistant,
			1,
			0,
			1,
			1,
			TestConnectionAuthenticator(),
			object : ConnectionAuthenticationAcceptor<Int> {
				override fun acceptAuthentication(channel: InternalChannel, identity: Int): InternalChannelProcessor = throw UnsupportedOperationException()
			},
			object : ConnectionRecoveryAcceptor {
				override fun acceptRecovery(connectionId: Long): ServerConnection<*>? = null
			}
		)
	}
	
	@Test(expected = IllegalArgumentException::class)
	fun `Coverage bad shutdownTimeoutSeconds`() {
		Channels(
			Sluice(false),
			GLOBAL_BYTE_BUFFER_POOL,
			FakeTemporalAssistant,
			1,
			1,
			-1,
			1,
			TestConnectionAuthenticator(),
			object : ConnectionAuthenticationAcceptor<Int> {
				override fun acceptAuthentication(channel: InternalChannel, identity: Int): InternalChannelProcessor = throw UnsupportedOperationException()
			},
			object : ConnectionRecoveryAcceptor {
				override fun acceptRecovery(connectionId: Long): ServerConnection<*>? = null
			}
		)
	}
	
	@Test(expected = IllegalArgumentException::class)
	fun `Coverage bad stopTimeoutSeconds`() {
		Channels(
			Sluice(false),
			GLOBAL_BYTE_BUFFER_POOL,
			FakeTemporalAssistant,
			1,
			1,
			1,
			0,
			TestConnectionAuthenticator(),
			object : ConnectionAuthenticationAcceptor<Int> {
				override fun acceptAuthentication(channel: InternalChannel, identity: Int): InternalChannelProcessor = throw UnsupportedOperationException()
			},
			object : ConnectionRecoveryAcceptor {
				override fun acceptRecovery(connectionId: Long): ServerConnection<*>? = null
			}
		)
	}
}