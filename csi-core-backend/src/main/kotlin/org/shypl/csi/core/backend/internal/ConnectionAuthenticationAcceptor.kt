package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.internal.InternalChannelProcessor
import org.shypl.csi.core.internal.InternalChannel

internal interface ConnectionAuthenticationAcceptor<I : Any> {
	fun acceptAuthentication(channel: InternalChannel, identity: I): InternalChannelProcessor
}