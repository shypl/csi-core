package org.shypl.csi.core.server.internal

internal object NothingServerChannelReleaser : ServerChannelReleaser {
	override fun releaseServerChannel(channel: ServerChannel) {
		throw UnsupportedOperationException()
	}
}
