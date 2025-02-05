package org.shypl.csi.core.backend

import org.shypl.csi.core.backend._test.server
import kotlin.test.Test

class BehaviorChannel {
	@Test
	fun `Activity timeout expired`() {
		server {
			channel {
				receiveData("33")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Empty input`() {
		server {
			channel {
				sendData {}
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Big input`() {
		server {
			channel {
				sendData {
					writeArray(ByteArray(1024 * 1024))
				}
				receiveData("31")
				receiveClose()
			}
		}
	}
}