package org.shypl.csi.core.server._test

import org.shypl.csi.core.server.internal.ServerConnection
import org.shypl.csi.core.server.internal.ServerConnectionReleaser

internal object EmptyServerConnectionReleaser : ServerConnectionReleaser<Int> {
	override fun releaseServerConnection(connection: ServerConnection<Int>) {}
}

