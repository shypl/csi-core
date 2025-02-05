package org.shypl.csi.core.backend

import org.shypl.csi.core.Connection

interface ConnectionAcceptor<I : Any> {
	fun acceptConnection(identity: I, connection: Connection): ConnectionHandler
}

