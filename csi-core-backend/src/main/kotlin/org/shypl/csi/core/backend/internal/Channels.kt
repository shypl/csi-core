package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler
import org.shypl.csi.core.internal.DummyChannelHandler
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.csi.core.backend.ChannelAcceptor
import org.shypl.csi.core.backend.ConnectionAuthenticator
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.putInt
import org.shypl.tool.lang.waitWhileLater
import org.shypl.tool.logging.info
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.logging.trace
import org.shypl.tool.logging.warn
import org.shypl.tool.utils.Sluice
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

internal class Channels<I : Any>(
	private val sluice: Sluice,
	private val byteBuffers: ObjectPool<ByteBuffer>,
	private val assistant: TemporalAssistant,
	serverVersion: Int,
	private val activityTimeoutSeconds: Int,
	private val shutdownTimeoutSeconds: Int,
	private val stopTimeoutSeconds: Int,
	authenticator: ConnectionAuthenticator<I>,
	authenticationAcceptor: ConnectionAuthenticationAcceptor<I>,
	recoveryAcceptor: ConnectionRecoveryAcceptor
) : ChannelAcceptor, ServerChannelReleaser {
	
	private val logger = ownLogger
	private val _size = AtomicInteger()
	private val channels = ConcurrentHashMap.newKeySet<ServerChannel>()
	private val receptionProcessor = ReceptionChannelProcessor(
		VersionValidatorChannelProcessor(serverVersion, AuthenticationChannelProcessor(authenticator, authenticationAcceptor)),
		RecoveryChannelProcessor(recoveryAcceptor)
	)
	
	val size: Int
		get() = _size.get()
	
	init {
		require(activityTimeoutSeconds > 0)
		require(shutdownTimeoutSeconds >= 0)
		require(stopTimeoutSeconds > 0)
	}
	
	override fun acceptChannel(channel: Channel): ChannelHandler {
		logger.trace { "Accept channel ${channel.id}" }
		
		sluice.pass {
			_size.getAndIncrement()
			val delegate = ServerChannelImpl(channel, receptionProcessor, byteBuffers, assistant, activityTimeoutSeconds, this)
			channels.add(delegate)
			return delegate
		}
		
		logger.trace { "Release channel ${channel.id} on shutdown" }
		channel.send(ProtocolMarker.SERVER_CLOSE_SHUTDOWN)
		channel.close()
		
		return DummyChannelHandler
	}
	
	override fun releaseServerChannel(channel: ServerChannel) {
		logger.trace { "Release channel ${channel.id}" }
		_size.getAndDecrement()
		
		channels.remove(channel)
	}
	
	fun stop() {
		if (_size.get() != 0) {
			
			if (shutdownTimeoutSeconds != 0) {
				logger.info { "Has $size channels, shutdown after $shutdownTimeoutSeconds seconds" }
				
				val message = ByteArray(5)
				message[0] = ProtocolMarker.SERVER_SHUTDOWN_TIMEOUT
				message.putInt(1, shutdownTimeoutSeconds)
				channels.forEach { it.send(message) }
				
				waitWhileLater(shutdownTimeoutSeconds * 1000, 100) { _size.get() != 0 }
			}
			
			if (_size.get() != 0) {
				logger.info { "Has $size channels, close them within $stopTimeoutSeconds seconds" }
				
				channels.forEach {
					it.closeWithMarker(ProtocolMarker.SERVER_CLOSE_SHUTDOWN)
				}
				
				if (waitWhileLater(stopTimeoutSeconds * 1000) { _size.get() != 0 }) {
					logger.warn { "Not all channels closed, $size left, ignore them" }
					channels.clear()
				}
			}
		}
	}
}