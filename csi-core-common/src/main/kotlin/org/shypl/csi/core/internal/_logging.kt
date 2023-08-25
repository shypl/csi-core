package org.shypl.csi.core.internal

import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.io.readArray
import org.shypl.tool.lang.toHexString

fun formatLoggerMessageBytes(prefix: String, data: Byte): String {
	return StringBuilder(prefix.length + 6)
		.append(prefix)
		.append("1B: ")
		.also { data.toHexString(it) }
		.toString()
}

fun formatLoggerMessageBytes(prefix: String, data: ByteArray): String {
	return formatLoggerMessageBytes(prefix, data, 0, data.size)
}

fun formatLoggerMessageBytes(prefix: String, data: InputByteBuffer): String {
	val size = data.readableSize
	val view = data.arrayView
	return if (view == null) {
		val array = data.readArray()
		data.backRead(size)
		formatLoggerMessageBytes(prefix, array, 0, size)
	}
	else formatLoggerMessageBytes(prefix, view.array, view.readerIndex, size)
}

private fun formatLoggerMessageBytes(prefix: String, array: ByteArray, offset: Int, size: Int): String {
	return when {
		size == 0 -> prefix + "0B"
		size > 32 -> prefix + size + "B"
		else      -> StringBuilder(prefix.length + 12 + size * 3)
			.append(prefix)
			.append(size)
			.append("B:")
			.apply {
				for (i in offset..<offset + size) {
					append(' ')
					array[i].toHexString(this)
				}
			}
			.toString()
	}
}