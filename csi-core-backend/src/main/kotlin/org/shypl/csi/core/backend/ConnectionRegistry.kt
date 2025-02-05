package org.shypl.csi.core.backend

interface ConnectionRegistry<I : Any> {
	fun put(identity: I, handler: () -> Unit)
	
	fun remove(identity: I)
}