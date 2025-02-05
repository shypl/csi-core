package org.shypl.csi.core.backend._test

import org.shypl.csi.core.backend.internal.ServerConnection
import org.shypl.csi.core.backend.internal.ServerConnectionReleaser

internal object EmptyServerConnectionReleaser : ServerConnectionReleaser<Int> {
	override fun releaseServerConnection(connection: ServerConnection<Int>) {}
}

