package org.shypl.csi.core.server._test

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.TimeUnit

object NowTemporalAssistant : TemporalAssistant {
	override fun execute(code: () -> Unit) {
		code.invoke()
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		code.invoke()
		return Cancelable.DUMMY
	}
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		code.invoke()
		return Cancelable.DUMMY
	}
	
	override fun charge(code: () -> Unit): Cancelable {
		code.invoke()
		return Cancelable.DUMMY
	}
	
	override fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		code.invoke()
		return Cancelable.DUMMY
	}
	
	override fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		code.invoke()
		return Cancelable.DUMMY
	}
}