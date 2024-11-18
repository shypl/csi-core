package org.shypl.csi.core.server

import org.shypl.csi.core.server.internal.Channels
import org.shypl.csi.core.server.internal.Connections
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.utils.Sluice
import org.shypl.tool.utils.Stoppable
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool
import java.io.Closeable
import kotlin.random.Random

class Server<I : Any>(
	assistant: TemporalAssistant,
	byteBuffers: ObjectPool<ByteBuffer>,
	connectionAuthenticator: ConnectionAuthenticator<I>,
	connectionAcceptor: ConnectionAcceptor<I>,
	gate: ChannelGate,
	shutdownTimeoutSeconds: Int = 0,
	version: Int = 0,
	channelActivityTimeoutSeconds: Int = 30,
	channelStopTimeoutSeconds: Int = 60 * 10,
	connectionStopTimeoutSeconds: Int = 60 * 10,
	connectionIdGenerator: () -> Long = Random.Default::nextLong,
	connectionRegistry: ConnectionRegistry<I> = DummyConnectionRegistry()
) : Stoppable {
	private val logger = ownLogger
	private val gate: Closeable
	
	private val sluice = Sluice()
	private val connectionHolder = Connections(
		assistant,
		byteBuffers,
		connectionAcceptor,
		channelActivityTimeoutSeconds,
		connectionStopTimeoutSeconds,
		connectionIdGenerator,
		connectionRegistry
	)
	private val channelHolder = Channels(
		sluice,
		byteBuffers,
		assistant,
		version,
		channelActivityTimeoutSeconds,
		shutdownTimeoutSeconds,
		channelStopTimeoutSeconds,
		connectionAuthenticator,
		connectionHolder,
		connectionHolder
	)
	
	val running: Boolean
		get() = sluice.opened
	
	val channels: Int
		get() = channelHolder.size
	
	val connections: Int
		get() = connectionHolder.size
	
	init {
		logger.debug("Starting")
		this.gate = gate.openGate(channelHolder)
		logger.debug("Started")
	}
	
	override fun stop() {
		if (sluice.close()) {
			logger.debug("Stopping")
			
			channelHolder.stop()
			connectionHolder.stop()
			gate.close()
			
			logger.debug("Stopped")
		}
	}
}
