package org.shypl.csi.core.client.internal

import org.shypl.csi.core.Connection
import org.shypl.csi.core.client.ConnectFailReason
import org.shypl.csi.core.client.ConnectionAcceptor
import org.shypl.csi.core.client.ConnectionHandler

internal open class NothingConnectionAcceptor : ConnectionAcceptor {
	override fun acceptConnection(connection: Connection): ConnectionHandler {
		throw UnsupportedOperationException()
	}
	
	override fun acceptFail(reason: ConnectFailReason) {
		throw UnsupportedOperationException()
	}
}