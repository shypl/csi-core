package org.shypl.csi.core.backend.internal

import org.shypl.csi.core.internal.InternalConnection

internal interface ServerConnection<I : Any> : InternalConnection {
	val identity: I
}

