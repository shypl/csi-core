package org.shypl.csi.core.backend.internal

internal interface ServerConnectionReleaser<I : Any> {
	fun releaseServerConnection(connection: ServerConnection<I>)
}