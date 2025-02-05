package org.shypl.csi.core.backend.internal

interface ServerChannelReleaser {
	fun releaseServerChannel(channel: ServerChannel)
}