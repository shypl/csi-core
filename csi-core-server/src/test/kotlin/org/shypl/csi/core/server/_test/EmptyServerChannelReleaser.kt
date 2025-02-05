package org.shypl.csi.core.server._test

import org.shypl.csi.core.server.internal.ServerChannel
import org.shypl.csi.core.server.internal.ServerChannelReleaser

object EmptyServerChannelReleaser : ServerChannelReleaser {
	override fun releaseServerChannel(channel: ServerChannel) {}
}