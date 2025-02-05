package org.shypl.csi.core.server

import java.io.Closeable

interface ChannelGate {
	fun openGate(acceptor: ChannelAcceptor): Closeable
}

