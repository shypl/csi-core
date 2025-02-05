package org.shypl.csi.core.backend

interface ConnectionAuthenticator<I : Any> {
	fun authenticateConnection(data: ByteArray): I?
}

