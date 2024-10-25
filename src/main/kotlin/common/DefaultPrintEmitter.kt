package common

import emitter.Printer

class DefaultPrintEmitter : Printer {
    override fun print(message: String) {
        println(message)
    }

    fun emit(message: String): String = message
}
