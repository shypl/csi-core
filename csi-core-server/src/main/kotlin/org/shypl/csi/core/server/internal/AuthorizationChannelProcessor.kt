package org.shypl.csi.core.server.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.ChannelProcessorInputResult
import org.shypl.csi.core.internal.InternalChannel
import org.shypl.csi.core.internal.ProtocolMarker
import org.shypl.csi.core.server.ConnectionAuthorizer
import org.shypl.tool.io.InputByteBuffer
import org.shypl.tool.io.readArray

internal class AuthorizationChannelProcessor<I : Any>(
	private val authorizer: ConnectionAuthorizer<I>,
	private val acceptor: ConnectionAuthorizationAcceptor<I>
) : InternalChannelProcessor {
	
	override fun processChannelInput(channel: InternalChannel, buffer: InputByteBuffer): ChannelProcessorInputResult {
		if (buffer.isReadable(4)) {
			val size = buffer.readInt()
			if (buffer.isReadable(size)) {
				val clientId = authorizer.authorizeConnection(buffer.readArray(size))
				
				if (clientId == null) {
					channel.closeWithMarker(ProtocolMarker.SERVER_CLOSE_AUTHORIZATION)
					return ChannelProcessorInputResult.BREAK
				}
				
				val processor = acceptor.acceptAuthorization(channel, clientId)
				
				channel.useProcessor(processor)
				return ChannelProcessorInputResult.CONTINUE
			}
			else {
				buffer.backRead(4)
			}
		}
		
		return ChannelProcessorInputResult.BREAK
	}
	
	override fun processChannelClose(channel: InternalChannel, interrupted: Boolean) {}
}

