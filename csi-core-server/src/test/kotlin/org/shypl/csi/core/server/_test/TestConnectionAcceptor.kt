package org.shypl.csi.core.server._test

import org.shypl.csi.core.Connection
import org.shypl.csi.core.server.ConnectionAcceptor
import org.shypl.csi.core.server.ConnectionHandler

class TestConnectionAcceptor : ConnectionAcceptor<Int> {
	override fun acceptConnection(identity: Int, connection: Connection): ConnectionHandler {
		return TestConnectionHandler(connection)
	}
}