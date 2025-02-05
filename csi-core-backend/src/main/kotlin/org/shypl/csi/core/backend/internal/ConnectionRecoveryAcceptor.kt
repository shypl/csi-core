package org.shypl.csi.core.backend.internal

internal interface ConnectionRecoveryAcceptor {
	fun acceptRecovery(connectionId: Long): ServerConnection<*>?
}
