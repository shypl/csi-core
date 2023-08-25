package org.shypl.csi.core.server._test

import org.shypl.tool.io.ArrayByteBuffer
import org.shypl.tool.io.readArray
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.assistant.ExecutorTemporalAssistant
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

fun assistant(size: Int = 1, name: String = "test"): TemporalAssistant {
	return ExecutorTemporalAssistant(
		if (size == 1) {
			Executors.newSingleThreadScheduledExecutor { r -> Thread(r, "assistant-$name") }
		}
		else {
			Executors.newScheduledThreadPool(size, object : ThreadFactory {
				private val counter = AtomicInteger()
				override fun newThread(r: Runnable) = Thread(r, "assistant-pool-$name-${counter.incrementAndGet()}")
			})
		}
	) { m, e -> System.err.println(m + "\n" + e?.stackTraceToString()) }
}

fun stringToByteBuffer(s: String): ArrayByteBuffer {
	val data = ArrayByteBuffer()
	var i = 0
	val l = s.length
	var b = ""
	var q = 0
	while (i < l) {
		val c = s[i++]
		if (c != ' ') {
			++q
			if (q == 2 || c != '0') {
				b += c
				if (q == 2) {
					data.writeByte(b.toInt(16).toByte())
					q = 0
					b = ""
				}
			}
		}
	}
	return data
}

fun stringToByteArray(s: String): ByteArray {
	return stringToByteBuffer(s).readArray()
}

fun threadPoolFactory(name: String): ThreadFactory {
	return object : ThreadFactory {
		private val counter = AtomicInteger()
		
		override fun newThread(r: Runnable): Thread {
			return Thread(r, "$name-${counter.incrementAndGet()}")
		}
	}
}