package org.shypl.csi.core.server

interface ConnectionAuthorizer<I : Any> {
	fun authorizeConnection(data: ByteArray): I?
}

