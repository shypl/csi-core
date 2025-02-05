package org.shypl.csi.core.backend._test

import org.shypl.csi.core.Channel
import org.shypl.csi.core.backend.ChannelAcceptor
import org.shypl.csi.core.backend.ChannelGate
import java.io.Closeable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class TestChannelGate(private val errors: Errors) : ChannelGate, Closeable {
	private lateinit var acceptor: ChannelAcceptor
	
	private val channelIds = AtomicInteger()
	val channels = ConcurrentLinkedQueue<TestChannel>()
	
	val allChannelsClosed
		get() = channels.all(TestChannel::completed)
	
	override fun openGate(acceptor: ChannelAcceptor): Closeable {
		this.acceptor = acceptor
		return this
	}
	
	override fun close() {}
	
	fun openChannel(actions: ChannelActions.() -> Unit): Channel {
		val id = channelIds.incrementAndGet()
		val channel = TestChannel(id)
		channels.add(channel)
		
		var accepted = false
		
		thread(name = "channel-$id") {
			errors.observe {
				channel.bindHandler(acceptor.acceptChannel(channel))
				accepted = true
				ChannelActions(channel).apply(actions)
			}
		}
		
		while (!accepted) {
			Thread.sleep(10)
		}
		
		return channel
	}
}