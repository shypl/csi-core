package org.shypl.csi.core.backend

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler

interface ChannelAcceptor {
	fun acceptChannel(channel: Channel): ChannelHandler
}

