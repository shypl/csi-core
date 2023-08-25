package org.shypl.csi.core.internal

import org.shypl.csi.core.ProtocolBrokenException
import org.shypl.tool.io.ByteBuffer
import org.shypl.tool.io.DummyByteBuffer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.io.readArray
import org.shypl.tool.lang.alsoOnFalse
import org.shypl.tool.lang.make
import org.shypl.tool.logging.Logger
import org.shypl.tool.logging.debug
import org.shypl.tool.logging.ownLogger
import org.shypl.tool.logging.trace
import org.shypl.tool.logging.wrap
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import org.shypl.tool.utils.pool.ObjectPool
import org.shypl.tool.utils.worker.LivingWorker

abstract class InternalChannelImpl(
	private var channel: org.shypl.csi.core.Channel,
	private var processor: InternalChannelProcessor,
	private var byteBuffers: ObjectPool<ByteBuffer>,
	private var assistant: TemporalAssistant,
	activityTimeoutSeconds: Int,
) : InternalChannel, org.shypl.csi.core.ChannelHandler {
	
	private val logger: Logger = ownLogger.wrap("[${channel.id}] ")
	
	private val worker = LivingWorker(assistant, ::syncHandleError)
	
	private var inputBuffer = byteBuffers.take()
	private var outputBuffer = byteBuffers.take()
	
	@Volatile private var activity = true
	private var activeChecker = assistant.repeat(activityTimeoutSeconds * 1000, ::checkActivity)
	
	override val id: Any
		get() = channel.id
	
	override fun send(data: Byte) {
		logger.trace { "Schedule send 1B" }
		
		worker.accessOrDeferOnLive({ outputBuffer.writeByte(data) }, {
			logger.debug { formatLoggerMessageBytes("Send ", data) }
			channel.send(data)
		})
	}
	
	override fun send(data: ByteArray) {
		logger.trace { "Schedule send ${data.size}B" }
		
		worker.accessOrDeferOnLive({ outputBuffer.writeArray(data) }, {
			logger.debug { formatLoggerMessageBytes("Send ", data) }
			channel.send(data)
		})
	}
	
	override fun send(data: InputByteBuffer) {
		logger.trace { "Schedule send ${data.readableSize}B" }
		
		if (worker.alive) {
			if (worker.accessible) {
				outputBuffer.writeBuffer(data)
			}
			else {
				data.readArray().also { bytes ->
					worker.defer {
						if (worker.alive) {
							logger.debug { formatLoggerMessageBytes("Send ", bytes) }
							channel.send(bytes)
						}
					}
				}
			}
		}
		else {
			data.skipRead()
		}
	}
	
	override fun close() {
		logger.trace { "Schedule close" }
		
		worker.accessOrDeferOnLive {
			syncClose(false)
		}
	}
	
	override fun useProcessor(processor: InternalChannelProcessor) {
		if (worker.accessible && worker.alive) {
			syncUseProcessor(processor)
		}
		else {
			throw IllegalStateException()
		}
	}
	
	override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {
		if (worker.accessible && worker.alive) {
			syncUseProcessor(processor)
			activeChecker.cancel()
			activeChecker = assistant.repeat(activityTimeoutSeconds * 1000, ::checkActivity)
		}
		else {
			throw IllegalStateException()
		}
	}
	
	override fun closeWithMarker(marker: Byte) {
		logger.trace { "Schedule close with marker ${ProtocolMarker.toString(marker)}" }
		
		worker.accessOrDeferOnLive {
			send(marker)
			syncClose(false)
		}
	}
	
	override fun handleChannelInput(data: InputByteBuffer) {
		logger.trace { "Handle channel input ${data.readableSize}B" }
		
		activity = true
		
		worker.withCaptureOnLive {
			inputBuffer.writeBuffer(data)
			syncProcessInput()
		} alsoOnFalse {
			val array = data.readArray()
			
			worker.executeOnLive {
				inputBuffer.writeArray(array)
				syncProcessInput()
			}
		}
	}
	
	override fun handleChannelClose() {
		logger.trace { "Handle channel close" }
		
		worker.executeOnLive {
			syncClose(true)
		}
	}
	
	protected abstract fun processClose()
	
	private fun checkActivity() {
		worker.executeOnLive {
			if (activity) {
				activity = false
			}
			else {
				logger.debug { "Activity timeout expired" }
				activeChecker.cancel()
				send(ProtocolMarker.CLOSE_ACTIVITY_TIMEOUT)
				syncClose(true)
			}
		}
	}
	
	private fun syncUseProcessor(processor: InternalChannelProcessor) {
		this.processor = processor
	}
	
	private fun syncProcessInput() {
		logger.debug { formatLoggerMessageBytes("Process input ", inputBuffer) }
		
		loop@ while (worker.alive && inputBuffer.readable) {
			when (processor.processChannelInput(this, inputBuffer)) {
				ChannelProcessorInputResult.CONTINUE -> continue@loop
				
				ChannelProcessorInputResult.BREAK    -> {
					inputBuffer.flush()
					break@loop
				}
				
				ChannelProcessorInputResult.DEFER    -> {
					inputBuffer.flush()
					worker.defer(::syncProcessInput)
					break@loop
				}
			}
		}
		
		if (worker.alive && outputBuffer.readable) {
			if (worker.relaxed) {
				syncSendOutput()
			}
			else {
				worker.executeOnLive(::syncSendOutputIfNeeded)
			}
		}
	}
	
	private fun syncSendOutputIfNeeded() {
		if (outputBuffer.readable) {
			syncSendOutput()
		}
	}
	
	private fun syncSendOutput() {
		logger.debug { formatLoggerMessageBytes("Send ", outputBuffer) }
		channel.send(outputBuffer)
		
		if (outputBuffer.readable) {
			throw IllegalStateException("Output buffer must be read in full")
		}
		outputBuffer.clear()
	}
	
	private fun syncClose(interrupted: Boolean) {
		syncSendOutputIfNeeded()
		
		logger.debug { "Close ${interrupted.make("interrupted", "definitely")}" }
		
		worker.die()
		
		val p = processor
		syncUseProcessor(NothingChannelProcessor)
		
		activeChecker.cancel()
		channel.close()
		
		activeChecker = Cancelable.DUMMY
		
		p.processChannelClose(this, interrupted)
		
		processClose()
		
		byteBuffers.back(inputBuffer)
		byteBuffers.back(outputBuffer)
		
		channel = NothingChannel
		inputBuffer = DummyByteBuffer
		outputBuffer = DummyByteBuffer
		
		byteBuffers = NothingByteBufferPool
		assistant = NothingTemporalAssistant
	}
	
	private fun syncHandleError(message: String, error: Throwable?) {
		if (error is ProtocolBrokenException) {
			logger.warn("Protocol broken: $message", error)
			closeWithMarker(ProtocolMarker.CLOSE_PROTOCOL_BROKEN)
		}
		else {
			logger.error("Uncaught exception: $message", error)
			closeWithMarker(ProtocolMarker.CLOSE_ERROR)
		}
	}
}