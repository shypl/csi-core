package org.shypl.csi.core.backend._test

import org.shypl.csi.core.Connection
import org.shypl.csi.core.backend.ConnectionAcceptor
import org.shypl.csi.core.backend.ConnectionHandler

class TestConnectionAcceptor : ConnectionAcceptor<Int> {
	override fun acceptConnection(identity: Int, connection: Connection): ConnectionHandler {
		return TestConnectionHandler(connection)
	}
}