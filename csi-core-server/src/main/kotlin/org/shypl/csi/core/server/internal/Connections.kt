package org.shypl.csi.core.server.internal

import org.shypl.csi.core.Connection
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.server.ConnectionAcceptor
import org.shypl.csi.core.server.ConnectionRegistry
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.lang.waitWhileLater
import org.shypl.tool.logging.info
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.logging.trace
import org.shypl.tool.logging.warn
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal class Connections<I : Any>(
	private val assistant: TemporalAssistant,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val acceptor: ConnectionAcceptor<I>,
	private val activityTimeoutSeconds: Int,
	private val stopTimeoutSeconds: Int,
	private val idGenerator: () -> Long,
	private val registry: ConnectionRegistry<I>,
) : ConnectionAuthenticationAcceptor<I>, ConnectionRecoveryAcceptor, ServerConnectionReleaser<I> {
	
	private val logger = ownLogger
	private val _size = AtomicInteger()
	private val identities = ConcurrentHashMap<Long, I>()
	private val connections = ConcurrentHashMap<I, ServerConnection<I>>()
	
	val size: Int
		get() = _size.get()
	
	init {
		require(activityTimeoutSeconds > 0)
		require(stopTimeoutSeconds > 0)
	}
	
	override fun acceptAuthentication(channel: InternalChannel, identity: I): InternalChannelProcessor {
		logger.trace { "Accept connection $identity" }
		
		var connectionId: Long
		do {
			connectionId = idGenerator.invoke()
		}
		while (connectionId == 0L || identities.putIfAbsent(connectionId, identity) != null)
		
		val connection = ServerConnectionImpl(
			connectionId,
			identity,
			channel,
			AcceptationConnectionProcessor(assistant, acceptor, activityTimeoutSeconds, identity),
			assistant,
			byteBuffers,
			this
		)
		
		_size.getAndIncrement()
		
		registry.put(identity) {
			connections.compute(identity, AcceptationMapper(connection))
		}
		
		return connection
	}
	
	override fun acceptRecovery(connectionId: Long): ServerConnection<I>? {
		val identity = identities[connectionId] ?: return null
		return connections[identity]?.takeIf { it.id == connectionId }
	}
	
	override fun releaseServerConnection(connection: ServerConnection<I>) {
		logger.trace { "Release connection ${connection.identity}" }
		
		_size.getAndDecrement()
		
		identities.remove(connection.id, connection.identity)
		connections.remove(connection.identity, connection)
		
		registry.remove(connection.identity)
	}
	
	fun stop() {
		if (_size.get() != 0) {
			logger.info { "Has $size connections, close them within $stopTimeoutSeconds seconds" }
			
			connections.values.forEach(Connection::close)
			
			if (waitWhileLater(stopTimeoutSeconds * 1000) { _size.get() != 0 }) {
				logger.warn { "Not all connections closed, $size left, ignore them" }
				connections.clear()
			}
		}
	}
}