package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer

interface OutgoingMessage {
	val id: Int
	val size: Int
	val data: InputByteBuffer
}