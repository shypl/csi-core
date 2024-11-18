package org.shypl.csi.core.server

interface ConnectionAuthenticator<I : Any> {
	fun authenticateConnection(data: ByteArray): I?
}

