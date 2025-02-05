package org.shypl.csi.core.server

import org.shypl.csi.core.server._test.server
import kotlin.test.Test

class BehaviorReception {
	@Test
	fun `Close on definitely`() {
		server {
			channel {
				sendData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Close on protocol broken`() {
		server {
			channel {
				sendData("31")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Close on error`() {
		server {
			channel {
				sendData("32")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Invalid flag`() {
		server {
			channel {
				sendData("01")
				receiveData("31")
				receiveClose()
			}
		}
	}
}