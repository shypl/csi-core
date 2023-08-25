package org.shypl.csi.core.server.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannel

internal interface ConnectionAuthorizationAcceptor<I : Any> {
	fun acceptAuthorization(channel: InternalChannel, identity: I): InternalChannelProcessor
}