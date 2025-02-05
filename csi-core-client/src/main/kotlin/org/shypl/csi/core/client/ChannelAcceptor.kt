package org.shypl.csi.core.client

import org.shypl.csi.core.Channel
import org.shypl.csi.core.ChannelHandler

interface ChannelAcceptor {
	fun acceptChannel(channel: Channel): ChannelHandler
	
	fun acceptFail()
}