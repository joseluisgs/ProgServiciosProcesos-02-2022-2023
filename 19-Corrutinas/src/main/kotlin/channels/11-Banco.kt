package channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import log

/**
 * con actor podemos hacer un monitor o porteger un estado
 */
sealed class CuentaBancariaMessage
class Saldo(val cantidad: CompletableDeferred<Long>) : CuentaBancariaMessage()
class Deposito(val cantidad: Long) : CuentaBancariaMessage()
class Retiro(val cantidad: Long, val estaPermitido: CompletableDeferred<Boolean>) : CuentaBancariaMessage()


// Nuestro manager solo recibe cosas!!!
fun CoroutineScope.cuentaBancariaManager(
    saldoInicial: Long
) = actor<CuentaBancariaMessage> {
    var saldo = saldoInicial
    for (message in channel) {
        when (message) {
            is Saldo -> message.cantidad.complete(saldo)
            is Deposito -> {
                saldo += message.cantidad
                log("Deposito de ${message.cantidad} realizado")
            }

            is Retiro -> {
                val permitido = saldo >= message.cantidad
                if (permitido) {
                    saldo -= message.cantidad
                    log("Retiro de ${message.cantidad} realizado")
                } else {
                    log("Retiro de ${message.cantidad} no permitido")
                }
                message.estaPermitido.complete(permitido)
            }
        }
    }
}


// Ahora vamos a crear distintos mecanismos de acceso al estado!!
suspend fun SendChannel<CuentaBancariaMessage>.deposito(nombre: String, cantidad: Long) {
    send(Deposito(cantidad))
    log("$nombre deposito $cantidad")
}

suspend fun SendChannel<CuentaBancariaMessage>.retiro(nombre: String, cantidad: Long) {
    val estado = CompletableDeferred<Boolean>().let {
        send(Retiro(cantidad, it))
        if (it.await()) {
            log("$nombre retiro $cantidad")
        } else {
            log("$nombre no pudo retirar $cantidad")
        }
    }
}

suspend fun SendChannel<CuentaBancariaMessage>.saldo(nombre: String): Long {
    val cantidad = CompletableDeferred<Long>().let {
        send(Saldo(it))
        it.await()
    }
    log("$nombre tiene $cantidad")
    return cantidad
}

fun main() = runBlocking<Unit> {
    val cuentaBancaria = cuentaBancariaManager(100)
    withContext(Dispatchers.Default) {
        val j1 = launch {
            cuentaBancaria.deposito("Cliente Pepe", 50)
            cuentaBancaria.saldo("Cliente Pepe")
        }

        val j2 = launch {
            cuentaBancaria.retiro("Cliente Juan", 100)
            cuentaBancaria.saldo("Cliente Juan")
        }

        val j3 = launch {
            cuentaBancaria.retiro("Cliente Maria", 100)
            cuentaBancaria.saldo("Cliente Maria")

        }

        j1.join()
        j2.join()
        j3.join()
        cuentaBancaria.close()
    }
}



