package common

import emitter.Printer

class DefaultPrintEmitter: Printer {

    override fun print(message: String): Unit {
        println(message)
    }

    fun emit(message: String): String {
        return message
    }
}