package org.shypl.csi.core.backend

import java.io.Closeable

interface ChannelGate {
	fun openGate(acceptor: ChannelAcceptor): Closeable
}

