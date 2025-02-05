package org.shypl.csi.core.backend

import org.shypl.csi.core.Channel
import org.shypl.csi.core.backend._test.EmptyChannel
import org.shypl.csi.core.backend._test.EmptyChannelProcessor
import org.shypl.csi.core.backend._test.EmptyServerChannelReleaser
import org.shypl.csi.core.backend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.backend._test.assistant
import org.shypl.csi.core.backend.internal.ServerChannel
import org.shypl.csi.core.backend.internal.ServerChannelImpl
import org.shypl.csi.core.backend.internal.ServerChannelReleaser
import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.lang.waitWhile
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CoverageServerChannelImpl {
	
	@Test
	fun `Coverage checkActivity`() {
		var opened = true
		var callback: () -> Unit = {}
		
		val channel = ServerChannelImpl(
			EmptyChannel,
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			object : TemporalAssistant {
				override fun execute(code: () -> Unit) {}
				
				override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
					callback = code
					return Cancelable.DUMMY
				}
				
				override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable = Cancelable.DUMMY
				
				override fun charge(code: () -> Unit): Cancelable = Cancelable.DUMMY
				
				override fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable = Cancelable.DUMMY
				
				override fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable = Cancelable.DUMMY
			},
			1,
			object : ServerChannelReleaser {
				override fun releaseServerChannel(channel: ServerChannel) {
					opened = false
				}
			}
		)
		
		channel.close()
		callback.invoke()
		
		waitWhile(1000) { opened }
		
		callback.invoke()
	}
	
	@Test
	fun `Coverage close`() {
		var opened = true
		val channel = ServerChannelImpl(
			object : Channel {
				override val id: Any = 0
				
				override fun send(data: Byte) {}
				
				override fun send(data: ByteArray) {}
				
				override fun send(data: InputByteBuffer) {
					sleep(100)
					data.skipRead()
				}
				
				override fun close() {
					opened = false
				}
				
			},
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1,
			EmptyServerChannelReleaser
		)
		
		channel.closeWithMarker(0)
		channel.close()
		channel.handleChannelInput(ArrayByteBuffer())
		channel.handleChannelClose()
		
		waitWhile(1000) { opened }
		
		channel.close()
		channel.handleChannelClose()
		channel.handleChannelInput(ArrayByteBuffer())
	}
	
	@Test
	fun `Coverage output buffer must be read in full`() {
		val channel = ServerChannelImpl(
			object : Channel {
				override val id: Any = 0
				
				override fun send(data: Byte) {}
				
				override fun send(data: ByteArray) {}
				
				override fun send(data: InputByteBuffer) {}
				
				override fun close() {}
				
			},
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1,
			EmptyServerChannelReleaser
		)
		
		channel.closeWithMarker(0)
	}
	
	@Test(expected = IllegalStateException::class)
	fun `Illegal useProcessor`() {
		var opened = true
		val channel = ServerChannelImpl(
			EmptyChannel,
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1, object : ServerChannelReleaser {
				override fun releaseServerChannel(channel: ServerChannel) {
					assertFailsWith<IllegalStateException> {
						channel.useProcessor(EmptyChannelProcessor)
					}
					sleep(10)
					opened = false
				}
			})
		
		channel.close()
		
		waitWhile(1000) { opened }
		
		channel.useProcessor(EmptyChannelProcessor)
	}
	
	
	@Test
	fun `Send byte after close`() {
		var opened = true
		val channel = ServerChannelImpl(
			EmptyChannel,
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1, object : ServerChannelReleaser {
				override fun releaseServerChannel(channel: ServerChannel) {
					sleep(10)
					opened = false
				}
			})
		
		channel.send(1)
		
		channel.close()
		channel.send(2)
		
		waitWhile(1000) { opened }
		
		channel.send(3)
	}
	
	@Test
	fun `Send array after close`() {
		var opened = true
		val channel = ServerChannelImpl(
			EmptyChannel,
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1, object : ServerChannelReleaser {
				override fun releaseServerChannel(channel: ServerChannel) {
					sleep(10)
					opened = false
				}
			})
		
		channel.close()
		channel.send(byteArrayOf(1))
		
		waitWhile(1000) { opened }
		
		channel.send(byteArrayOf(2))
	}
	
	@Test
	fun `Send buffer after close`() {
		var opened = true
		val buffer = ArrayByteBuffer { writeInt(1) }
		val channel = ServerChannelImpl(
			EmptyChannel,
			EmptyChannelProcessor,
			GLOBAL_BYTE_BUFFER_POOL,
			assistant(), 1, object : ServerChannelReleaser {
				override fun releaseServerChannel(channel: ServerChannel) {
					sleep(10)
					opened = false
				}
			})
		
		channel.close()
		channel.send(buffer)
		
		waitWhile(1000) { opened }
		
		assertFalse(buffer.readable)
		
		buffer.writeInt(2)
		channel.send(buffer)
		
		assertFalse(buffer.readable)
	}
}