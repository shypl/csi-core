package org.shypl.csi.core.server.internal

interface ServerChannelReleaser {
	fun releaseServerChannel(channel: ServerChannel)
}