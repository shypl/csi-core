package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.internal.ProtocolMarker
import java.util.function.BiFunction

internal class AcceptationMapper<I : Any>(
	private val connection: ServerConnection<I>
) : BiFunction<I, ServerConnection<I>?, ServerConnection<I>> {
	
	override fun apply(clientId: I, previous: ServerConnection<I>?): ServerConnection<I> {
		if (previous == null) {
			complete()
		}
		else {
			previous.closeWithMarker(ProtocolMarker.SERVER_CLOSE_CONCURRENT, ::complete)
		}
		return connection
	}
	
	private fun complete() {
		connection.accept()
	}
}