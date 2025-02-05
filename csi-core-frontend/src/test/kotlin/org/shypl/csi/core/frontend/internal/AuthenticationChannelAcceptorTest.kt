package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.frontend.ConnectFailReason
import org.shypl.csi.core.frontend._test.FnChannel
import org.shypl.csi.core.frontend._test.GLOBAL_ASSISTANT
import org.shypl.csi.core.frontend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.frontend._test.assertEqualsBytes
import org.shypl.csi.core.frontend._test.gate
import org.shypl.csi.core.frontend._test.waitWhileSecond
import org.shypl.tool.io.readArray
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationChannelAcceptorTest {
	@Test
	fun `When accept fail then connect failed as REFUSED`() {
		var actualReason: ConnectFailReason? = null
		
		val acceptor = object : NothingConnectionAcceptor() {
			override fun acceptFail(reason: ConnectFailReason) {
				actualReason = reason
			}
		}
		
		AuthenticationChannelAcceptor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, 0, byteArrayOf(), acceptor, 1)
			.acceptFail()
		
		assertEquals(ConnectFailReason.REFUSED, actualReason)
	}
	
	@Test
	fun `When accept success then send auth request`() {
		var data: ByteArray? = null
		
		val acceptor = NothingConnectionAcceptor()
		
		AuthenticationChannelAcceptor(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {}, 7, byteArrayOf(0x42), acceptor, 1)
			.acceptChannel(FnChannel({
				data = readArray()
			}))
		
		waitWhileSecond { data == null }
		
		assertEqualsBytes("10  00 00 00 07  00 00 00 01  42", data)
	}
}