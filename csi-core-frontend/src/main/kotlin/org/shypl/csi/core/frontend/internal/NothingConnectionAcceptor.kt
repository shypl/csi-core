package org.shypl.csi.core.frontend.internal

import org.shypl.csi.core.Connection
import org.shypl.csi.core.frontend.ConnectFailReason
import org.shypl.csi.core.frontend.ConnectionAcceptor
import org.shypl.csi.core.frontend.ConnectionHandler

internal open class NothingConnectionAcceptor : ConnectionAcceptor {
	override fun acceptConnection(connection: Connection): ConnectionHandler {
		throw UnsupportedOperationException()
	}
	
	override fun acceptFail(reason: ConnectFailReason) {
		throw UnsupportedOperationException()
	}
}