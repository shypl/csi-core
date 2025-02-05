package org.shypl.csi.core.frontend

import org.shypl.csi.core.BaseConnectionHandler

interface ConnectionHandler : BaseConnectionHandler {
	fun handleConnectionLost(): ConnectionRecoveryHandler
	
	fun handleConnectionCloseTimeout(seconds: Int)
}