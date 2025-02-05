package org.shypl.csi.core.frontend

import org.shypl.csi.core.frontend.internal.AuthenticationChannelAcceptor
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
	private val authenticationTimeoutSeconds: Int = 300
) {
	fun connect(authenticationKey: ByteArray, connectionAcceptor: ConnectionAcceptor) {
		ownLogger.trace { formatLoggerMessageBytes("Connect with authentication key ", authenticationKey) }
		
		gate.openChannel(
			AuthenticationChannelAcceptor(
				assistant,
				byteBuffers,
				gate,
				version,
				authenticationKey,
				connectionAcceptor,
				authenticationTimeoutSeconds
			)
		)
	}
}

