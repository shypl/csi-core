package org.shypl.csi.core.backend._test

import org.shypl.csi.core.backend.internal.ServerChannel
import org.shypl.csi.core.backend.internal.ServerChannelReleaser

object EmptyServerChannelReleaser : ServerChannelReleaser {
	override fun releaseServerChannel(channel: ServerChannel) {}
}