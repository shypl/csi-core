package org.shypl.csi.core.internal

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.TimeUnit

object NothingTemporalAssistant : TemporalAssistant {
	override fun charge(code: () -> Unit): Cancelable {
		throw UnsupportedOperationException()
	}
	
	override fun execute(code: () -> Unit) {
		throw UnsupportedOperationException()
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		throw UnsupportedOperationException()
	}
	
	override fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		throw UnsupportedOperationException()
	}
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		throw UnsupportedOperationException()
	}
	
	override fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		throw UnsupportedOperationException()
	}
	
}