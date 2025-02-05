package org.shypl.csi.core.frontend

import org.shypl.csi.core.Connection

interface ConnectionAcceptor {
	fun acceptConnection(connection: Connection): ConnectionHandler
	
	fun acceptFail(reason: ConnectFailReason)
}