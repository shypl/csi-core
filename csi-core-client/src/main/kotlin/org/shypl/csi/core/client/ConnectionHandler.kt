package org.shypl.csi.core.client

import org.shypl.csi.core.BaseConnectionHandler

interface ConnectionHandler : BaseConnectionHandler {
	fun handleConnectionLost(): ConnectionRecoveryHandler
	
	fun handleConnectionCloseTimeout(seconds: Int)
}