package org.shypl.csi.core.backend.internal

internal object NothingServerChannelReleaser : ServerChannelReleaser {
	override fun releaseServerChannel(channel: ServerChannel) {
		throw UnsupportedOperationException()
	}
}
