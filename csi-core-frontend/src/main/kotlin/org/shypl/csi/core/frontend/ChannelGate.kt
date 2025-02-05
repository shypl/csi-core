package org.shypl.csi.core.frontend

interface ChannelGate {
	fun openChannel(acceptor: ChannelAcceptor)
}