package org.shypl.csi.core.backend

import org.junit.Test
import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.NothingInternalChannel
import org.shypl.csi.core.backend._test.FakeTemporalAssistant
import org.shypl.csi.core.backend._test.GLOBAL_BYTE_BUFFER_POOL
import org.shypl.csi.core.backend._test.NowTemporalAssistant
import org.shypl.csi.core.backend._test.TestConnectionAcceptor
import org.shypl.csi.core.backend.internal.Connections
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.utils.assistant.ExecutorTemporalAssistant
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.test.assertNull

class CoverageConnections {
	@Test(expected = IllegalArgumentException::class)
	fun `Coverage bad activityTimeoutSeconds`() {
		Connections(
			FakeTemporalAssistant,
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			0,
			1,
			Random.Default::nextLong,
			DummyConnectionRegistry()
		)
	}
	
	@Test(expected = IllegalArgumentException::class)
	fun `Coverage bad stopTimeoutSeconds`() {
		Connections(
			FakeTemporalAssistant,
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			1,
			0,
			Random.Default::nextLong,
			DummyConnectionRegistry()
		)
	}
	
	@Test
	fun `Coverage connectionIdGenerator gives invalid ids`() {
		val ids = mutableListOf<Long>(0, 1, 1, 2)
		val connections = Connections(
			FakeTemporalAssistant,
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			1,
			1,
			{
				ids.removeAt(0)
			},
			DummyConnectionRegistry()
		)
		
		connections.acceptAuthentication(NothingInternalChannel, 1)
		connections.acceptAuthentication(NothingInternalChannel, 1)
	}
	
	@Test
	fun `Coverage recovery connection on miss identity`() {
		val connections = Connections(
			NowTemporalAssistant,
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			1,
			1,
			{ 1 },
			DummyConnectionRegistry()
		)
		
		connections.acceptAuthentication(object : InternalChannel {
			override val id: Any = 1
			
			override fun useProcessor(processor: InternalChannelProcessor) {}
			
			override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {}
			
			override fun closeWithMarker(marker: Byte) {}
			
			override fun send(data: Byte) {}
			
			override fun send(data: ByteArray) {
				connections.acceptRecovery(1)
			}
			
			override fun send(data: InputByteBuffer) {
				data.skipRead()
			}
			
			override fun close() {}
		}, 1)
	}
	
	@Test
	fun `Coverage recovery connection on miss connection id`() {
		var i = 0L
		val connections = Connections(
			ExecutorTemporalAssistant(Executors.newSingleThreadScheduledExecutor()) { m, e -> System.err.println(m + "\n" + e?.stackTraceToString()) },
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			1,
			1,
			{ ++i },
			DummyConnectionRegistry()
		)
		
		connections.acceptAuthentication(object : InternalChannel {
			override val id: Any = 1
			
			override fun useProcessor(processor: InternalChannelProcessor) {}
			
			override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {}
			
			override fun closeWithMarker(marker: Byte) {
				connections.acceptRecovery(2)
			}
			
			override fun send(data: Byte) {}
			
			override fun send(data: ByteArray) {
				
				connections.acceptAuthentication(object : InternalChannel {
					override val id: Any = 1
					
					override fun useProcessor(processor: InternalChannelProcessor) {}
					
					override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {}
					
					override fun closeWithMarker(marker: Byte) {}
					
					override fun send(data: Byte) {}
					
					override fun send(data: ByteArray) {}
					
					override fun send(data: InputByteBuffer) {
						data.skipRead()
					}
					
					override fun close() {}
				}, 1)
				
			}
			
			override fun send(data: InputByteBuffer) {
				data.skipRead()
			}
			
			override fun close() {}
		}, 1)
	}
	
	@Test
	fun `Coverage recovery connection on bad connection id`() {
		var i = 0L
		val connections = Connections(
			ExecutorTemporalAssistant(Executors.newSingleThreadScheduledExecutor()) { m, e -> System.err.println(m + "\n" + e?.stackTraceToString()) },
			GLOBAL_BYTE_BUFFER_POOL,
			TestConnectionAcceptor(),
			1,
			1,
			{ ++i },
			DummyConnectionRegistry()
		)
		
		connections.acceptAuthentication(object : InternalChannel {
			override val id: Any = 1
			
			override fun useProcessor(processor: InternalChannelProcessor) {}
			
			override fun useProcessor(processor: InternalChannelProcessor, activityTimeoutSeconds: Int) {}
			
			override fun closeWithMarker(marker: Byte) {}
			
			override fun send(data: Byte) {}
			
			override fun send(data: ByteArray) {}
			
			override fun send(data: InputByteBuffer) {
				data.skipRead()
			}
			
			override fun close() {}
		}, 1)
		
		
		val identities = Connections::class.java.getDeclaredField("identities")
		identities.isAccessible = true
		@Suppress("UNCHECKED_CAST")
		(identities.get(connections) as MutableMap<Long, Int>)[2] = 1
		
		assertNull(connections.acceptRecovery(2))
	}
}