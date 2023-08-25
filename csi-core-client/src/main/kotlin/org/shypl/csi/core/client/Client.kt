package org.shypl.csi.core.client

import org.shypl.csi.core.client.internal.AuthorizationChannelAcceptor
import org.shypl.csi.core.internal.formatLoggerMessageBytes
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.logging.trace
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool

class Client(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val gate: ChannelGate,
	private val version: Int = 0,
	private val authorizationTimeoutSeconds: Int = 300
) {
	fun connect(authorizationKey: ByteArray, connectionAcceptor: ConnectionAcceptor) {
		ownLogger.trace { formatLoggerMessageBytes("Connect with authorization key ", authorizationKey) }
		
		gate.openChannel(
			AuthorizationChannelAcceptor(
				assistant,
				byteBuffers,
				gate,
				version,
				authorizationKey,
				connectionAcceptor,
				authorizationTimeoutSeconds
			)
		)
	}
}

