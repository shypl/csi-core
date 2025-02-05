package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.frontend.ConnectionHandler
import org.shypl.csi.core.frontend.ConnectionRecoveryHandler
import org.shypl.csi.core.frontend.DummyConnectionRecoveryHandler
import org.shypl.csi.core.frontend._test.FnChannel
import org.shypl.csi.core.frontend._test.GLOBAL_ASSISTANT
import org.shypl.csi.core.frontend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.frontend._test.assertEqualsBytes
import org.shypl.csi.core.frontend._test.buffer
import org.shypl.csi.core.frontend._test.gate
import org.shypl.csi.core.frontend._test.write
import org.shypl.csi.core.internal.Messages
import org.shypl.csi.core.internal.NothingChannel
import org.shypl.csi.core.internal.NothingInternalConnection
import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.lang.waitWhile
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClientMessagingConnectionProcessorTest {
	@Test
	fun `When channel closed then handle connection lost and stop pinger`() {
		
		var actualLost = false
		var actualPingerStopped = false
		
		val handler = object : ConnectionHandler {
			override fun handleConnectionLost(): ConnectionRecoveryHandler {
				actualLost = true
				return DummyConnectionRecoveryHandler()
			}
			
			override fun handleConnectionCloseTimeout(seconds: Int) {}
			override fun handleConnectionMessage(message: InputByteBuffer) {}
			override fun handleConnectionClose() {}
		}
		
		val assistant = object : TemporalAssistant by GLOBAL_ASSISTANT {
			override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
				return Cancelable { actualPingerStopped = true }
			}
		}
		
		val processor = ClientMessagingConnectionProcessor(
			handler, Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, assistant, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, NothingChannel
		)
		
		processor.processChannelInterrupt(NothingInternalConnection)
		
		assertTrue(actualLost)
		assertTrue(actualPingerStopped)
	}
	
	@Test
	fun `When connection recovery then start pinger`() {
		var actualPingerStarts = 0
		
		val assistant = object : TemporalAssistant by GLOBAL_ASSISTANT {
			override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
				++actualPingerStarts
				return Cancelable.DUMMY
			}
		}
		
		val processor = ClientMessagingConnectionProcessor(
			NothingConnectionHandler(), Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, assistant, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, NothingChannel
		)
		
		processor.processConnectionRecovery(NothingChannel)
		
		assertEquals(2, actualPingerStarts)
	}
	
	@Test
	fun `When connection recovery then stop pinger`() {
		var actualPingerStopped = false
		
		val assistant = object : TemporalAssistant by GLOBAL_ASSISTANT {
			override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
				return Cancelable { actualPingerStopped = true }
			}
		}
		
		val handler = object : ConnectionHandler {
			override fun handleConnectionLost(): ConnectionRecoveryHandler = DummyConnectionRecoveryHandler()
			override fun handleConnectionCloseTimeout(seconds: Int) {}
			override fun handleConnectionMessage(message: InputByteBuffer) {}
			override fun handleConnectionClose() {}
		}
		
		val processor = ClientMessagingConnectionProcessor(
			handler, Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, assistant, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, NothingChannel
		)
		
		processor.processConnectionClose()
		
		assertTrue(actualPingerStopped)
	}
	
	@Test
	fun `Ping communication`() {
		
		val actualOutput = ArrayByteBuffer()
		
		val channel = FnChannel({
			actualOutput.writeBuffer(this)
		})
		
		val processor = ClientMessagingConnectionProcessor(
			NothingConnectionHandler(), Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, channel
		)
		
		val processInputResult = processor.processChannelInput(channel, buffer("20"))
		
		assertTrue(processInputResult)
		
		waitWhile(2100) { actualOutput.readableSize < 2 }
		
		assertEqualsBytes("20 20", actualOutput)
	}
	
	@Test
	fun `When input SERVER_CLOSE_SHUTDOWN then channel closed`() {
		var actualChannelClose = false
		
		val channel = FnChannel(close = { actualChannelClose = true })
		
		val processor = ClientMessagingConnectionProcessor(
			NothingConnectionHandler(), Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, channel
		)
		
		processor.processChannelInput(channel, buffer("54"))
		
		assertTrue(actualChannelClose)
	}
	
	@Test
	fun `When input SERVER_SHUTDOWN_TIMEOUT then handle connection close timeout`() {
		var actualConnectionCloseTimeout = 0
		
		val handler = object : ConnectionHandler {
			override fun handleConnectionLost(): ConnectionRecoveryHandler = DummyConnectionRecoveryHandler()
			override fun handleConnectionCloseTimeout(seconds: Int) {
				actualConnectionCloseTimeout = seconds
			}
			
			override fun handleConnectionMessage(message: InputByteBuffer) {}
			override fun handleConnectionClose() {}
		}
		
		val processor = ClientMessagingConnectionProcessor(
			handler, Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, NothingChannel
		)
		
		val buffer = buffer { }
		
		buffer.write("40 00 00")
		processor.processChannelInput(NothingChannel, buffer)
		
		
		buffer.write("00 07")
		processor.processChannelInput(NothingChannel, buffer)
		
		assertEquals(7, actualConnectionCloseTimeout)
	}
	
	@Test
	fun `When input message then handle connection message`() {
		val actualMessage = ArrayByteBuffer()
		
		val handler = object : ConnectionHandler {
			override fun handleConnectionLost(): ConnectionRecoveryHandler = DummyConnectionRecoveryHandler()
			override fun handleConnectionCloseTimeout(seconds: Int) {}
			override fun handleConnectionMessage(message: InputByteBuffer) {
				actualMessage.writeBuffer(message)
			}
			
			override fun handleConnectionClose() {}
		}
		
		val processor = ClientMessagingConnectionProcessor(
			handler, Messages(GLOBAL_BYTE_BUFFER_POOL), ownLogger, GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, 1, gate { }, NothingChannel
		)
		
		processor.processChannelInput(NothingChannel, buffer("21  00 00 00 01  00 00 00 01  42"))
		
		assertEqualsBytes("42", actualMessage)
	}
}