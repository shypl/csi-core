package org.shypl.csi.core.server

import org.shypl.csi.core.server._test.TestApiMarker
import org.shypl.csi.core.server._test.server
import java.lang.Thread.sleep
import kotlin.test.Test

class BehaviorMessaging {
	@Test
	fun `Ping stay connection active`() {
		server {
			channel {
				authentication()
				
				sleep(1000)
				sendData("20")
				receiveData("20")
				
				sleep(1000)
				sendData("20")
				receiveData("20")
				
				sleep(1000)
				sendData("20")
				receiveData("20")
				
				sendData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Input message on closing connection`() {
		server {
			channel {
				authentication()
				
				sendData {
					writeByte(0x21)
					writeInt(1)
					writeInt(1)
					writeByte(TestApiMarker.CLOSE)
					
					writeByte(0x21)
					writeInt(2)
					writeInt(0)
				}
				
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Input message on outside closing connection`() {
		server {
			channel {
				authentication()
				
				sendMessageCloseSleep(1, 100)
				sendMessage(2) {}
				
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Input 2 messages on 1 frame`() {
		server {
			channel {
				authentication()
				
				sendData {
					writeByte(0x21)
					writeInt(1)
					writeInt(1)
					writeByte(0x41)
					
					writeByte(0x21)
					writeInt(2)
					writeInt(1)
					writeByte(TestApiMarker.CLOSE)
				}
				
				receiveMessageReceived(2)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Error on process message`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) { writeByte(TestApiMarker.ERROR) }
				receiveMessageReceived(1)
				receiveData("32")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Error on process message after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) { writeByte(TestApiMarker.CLOSE_ERROR) }
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	
	@Test
	fun `Reuse message from OutgoingMessageBuffer cache`() {
		server {
			channel {
				authentication()
				
				sendMessageEcho(1, 42)
				receiveMessageInt(1, 42)
				receiveMessageReceived(1)
				
				sendData {
					// bad message id not clear OutgoingMessageBuffer
					writeByte(0x22)
					writeInt(7)
				}
				
				sendData {
					writeByte(0x22)
					writeInt(1) // bad message id not clear OutgoingMessageBuffer
				}
				
				sendMessageEcho(2, 42)
				receiveMessageInt(2, 42)
				receiveMessageReceived(2)
				
				sendData {
					writeByte(0x22)
					writeInt(2)
				}
				
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Separated message`() {
		server {
			channel {
				authentication()
				
				sendData("21")
				sendData("00 00 00 01")
				sendData("00 00 00 01")
				sendData("FF")
				
				receiveMessageReceived(1)
				
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Separated received message`() {
		server {
			channel {
				authentication()
				
				sendData("22")
				sendData("00 00 00 01")
				
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Error on not all message data read`() {
		server {
			channel {
				authentication()
				
				sendData("21  00 00 00 01  00 00 00 02  FF FF")
				
				receiveMessageReceived(1)
				receiveData("32")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Invalid flag`() {
		server {
			channel {
				authentication()
				
				sendData("FF")
				
				receiveData("31")
				receiveClose()
			}
		}
	}
	
	//
	
	@Test
	fun `Send message byte`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BYTE)
					writeByte(0)
					writeByte(0)
				}
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				receiveMessageReceived(1)
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message byte parallel`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BYTE)
					writeByte(0)
					writeByte(1)
				}
				receiveMessageReceived(1)
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message byte after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BYTE)
					writeByte(1)
					writeByte(0)
				}
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Send message byte parallel after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BYTE)
					writeByte(1)
					writeByte(1)
				}
				
				receiveMessageReceived(1)
				receiveData(" 30")
				receiveClose()
			}
		}
	}
	
	//
	
	@Test
	fun `Send message array`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_ARRAY)
					writeByte(0)
					writeByte(0)
				}
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				receiveMessageReceived(1)
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message array parallel`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_ARRAY)
					writeByte(0)
					writeByte(1)
				}
				receiveMessageReceived(1)
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message array after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_ARRAY)
					writeByte(1)
					writeByte(0)
				}
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Send message array parallel after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_ARRAY)
					writeByte(1)
					writeByte(1)
				}
				
				receiveMessageReceived(1)
				receiveData(" 30")
				receiveClose()
			}
		}
	}
	
	//
	
	@Test
	fun `Send message buffer`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BUFFER)
					writeByte(0)
					writeByte(0)
				}
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				receiveMessageReceived(1)
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message buffer parallel`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BUFFER)
					writeByte(0)
					writeByte(1)
				}
				receiveMessageReceived(1)
				receiveData(" 21 00 00 00 01 00 00 00 01 07")
				closeDefinitely()
			}
		}
	}
	
	@Test
	fun `Send message buffer after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BUFFER)
					writeByte(1)
					writeByte(0)
				}
				receiveMessageReceived(1)
				receiveData("30")
				receiveClose()
			}
		}
	}
	
	@Test
	fun `Send message buffer parallel after close`() {
		server {
			channel {
				authentication()
				
				sendMessage(1) {
					writeByte(TestApiMarker.SEND_BUFFER)
					writeByte(1)
					writeByte(1)
				}
				
				receiveMessageReceived(1)
				receiveData(" 30")
				receiveClose()
			}
		}
	}
}