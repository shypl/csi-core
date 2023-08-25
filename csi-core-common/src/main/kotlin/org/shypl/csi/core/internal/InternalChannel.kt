package org.shypl.csi.core.internal

interface InternalChannel : org.shypl.csi.core.Channel {
	fun useProcessor(processor: InternalChannelProcessor)
	
	fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int)
	
	fun closeWithMarker(marker: Byte)
}