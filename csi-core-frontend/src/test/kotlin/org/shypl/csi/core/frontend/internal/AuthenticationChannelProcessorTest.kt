package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Connection
import org.shypl.csi.core.frontend.ConnectFailReason
import org.shypl.csi.core.frontend.ConnectionHandler
import org.shypl.csi.core.frontend._test.FnInternalChannel
import org.shypl.csi.core.frontend._test.GLOBAL_ASSISTANT
import org.shypl.csi.core.frontend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.frontend._test.buffer
import org.shypl.csi.core.frontend._test.gate
import org.shypl.csi.core.frontend._test.waitWhileSecond
import org.shypl.csi.core.frontend._test.write
import org.shypl.csi.core.internal.NothingInternalChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthenticationChannelProcessorTest {
	@Test
	fun `Accept connection on valid separated input data`() {
		var connectionId: Long? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptConnection(connection: Connection): ConnectionHandler {
				connectionId = connection.id
				return NothingConnectionHandler()
			}
		}
		
		val channel = FnInternalChannel()
		val processor = AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
		val buffer = buffer {}
		
		buffer.write("10  00 00 00 00 00")
		processor.processChannelInput(channel, buffer)
		
		buffer.write("00 00 07  00 00 00 01")
		processor.processChannelInput(channel, buffer)
		
		waitWhileSecond { connectionId == null }
		
		assertEquals(7, connectionId)
	}
	
	
	@Test
	fun `Connect fail as VERSION on marker SERVER_CLOSE_VERSION`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(close = { actualClosed = true })
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("50"))
		
		assertEquals(ConnectFailReason.VERSION, actualReason)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as AUTHENTICATION on marker SERVER_CLOSE_AUTHENTICATION`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(close = { actualClosed = true })
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("51"))
		
		assertEquals(ConnectFailReason.AUTHENTICATION, actualReason)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as REFUSED on marker SERVER_CLOSE_SHUTDOWN`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(close = { actualClosed = true })
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("54"))
		
		assertEquals(ConnectFailReason.REFUSED, actualReason)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as ERROR on marker CLOSE_ERROR`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(close = { actualClosed = true })
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("32"))
		
		assertEquals(ConnectFailReason.ERROR, actualReason)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as ERROR on marker CLOSE_PROTOCOL_BROKEN`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(close = { actualClosed = true })
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("31"))
		
		assertEquals(ConnectFailReason.ERROR, actualReason)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as REFUSED on marker SERVER_SHUTDOWN_TIMEOUT`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		var actualOutput: Byte? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(
			send = { actualOutput = this.readByte() },
			close = { actualClosed = true }
		)
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("40"))
		
		assertEquals(ConnectFailReason.REFUSED, actualReason)
		assertEquals(0x30, actualOutput)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as ERROR on invalid marker`() {
		var actualReason: ConnectFailReason? = null
		var actualClosed = false
		var actualOutput: Byte? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel(
			send = { actualOutput = this.readByte() },
			close = { actualClosed = true }
		)
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelInput(channel, buffer("77"))
		
		assertEquals(ConnectFailReason.ERROR, actualReason)
		assertEquals(0x31, actualOutput)
		assertTrue(actualClosed)
	}
	
	@Test
	fun `Connect fail as REFUSED on channel interrupted`() {
		var actualReason: ConnectFailReason? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
			.processChannelClose(NothingInternalChannel, true)
		
		assertEquals(ConnectFailReason.REFUSED, actualReason)
	}
	
	@Test
	fun `Connect fail as REFUSED on channel close`() {
		var actualReason: ConnectFailReason? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		val channel = FnInternalChannel()
		
		val processor = AuthenticationChannelProcessor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, acceptor)
		channel.useProcessor(processor)
		processor.processChannelInput(channel, buffer("30"))
		
		assertEquals(ConnectFailReason.REFUSED, actualReason)
	}
}
