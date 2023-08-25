package org.shypl.csi.core.server

interface ConnectionAuthorizer<I : Any> {
	fun authorizeConnection(key: ByteArray): I?
}

