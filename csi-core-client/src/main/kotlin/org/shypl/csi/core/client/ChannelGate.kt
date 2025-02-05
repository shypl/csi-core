package org.shypl.csi.core.client

interface ChannelGate {
	fun openChannel(acceptor: ChannelAcceptor)
}