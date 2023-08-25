package org.shypl.csi.core.client

import org.shypl.csi.core.client._test.GLOBAL_ASSISTANT
import org.shypl.csi.core.client._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.client._test.gate
import org.shypl.csi.core.client._test.waitWhileSecond
import org.shypl.csi.core.client.internal.NothingConnectionAcceptor
import kotlin.test.Test
import kotlin.test.assertTrue

class ClientTest {
	@Test
	fun `Call connect open channel`() {
		var called = false
		
		val client = Client(GLOBAL_ASSISTANT, GLOBAL_BYTE_BUFFER_POOL, gate {
			called = true
		})
		
		client.connect(byteArrayOf(), NothingConnectionAcceptor())
		
		waitWhileSecond { !called }
		
		assertTrue(called)
	}
}