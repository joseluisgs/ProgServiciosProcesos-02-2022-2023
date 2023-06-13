package models

import java.util.concurrent.Callable
import kotlin.system.measureTimeMillis

/**
 * Ejemplo de uso de hilos con Kotlin
 * Thread lanza hilos, podemos heredar de Hilos, o implementar
 * Runnable: para ejecutar código en un hilo
 * Callable: para ejecutar código en un hilo y devolver un valor
 * ExecutorService: para ejecutar hilos teniendo un pool de hilos fijos
 * y con ello optimizar recursos. Es decir, no creando hilos son razon!
 */

class Caja(val cliente: Cliente) : Runnable, Callable<Int> {
    override fun run() {
        var precioTotal = 0
        measureTimeMillis {
            println("Caja: ${Thread.currentThread().name} atendiendo a ${cliente.nombre} que tiene ${cliente.carro.productos.size} productos")
            cliente.carro.productos.forEachIndexed { index, producto ->
                println("Caja: ${Thread.currentThread().name} atendiendo a ${cliente.nombre}, producto: ${index + 1} con precio: ${producto.precio} €")
                Thread.sleep(producto.precio.toLong() * 1000)
                precioTotal += producto.precio
            }
            println("Caja: ${Thread.currentThread().name} terminó de atender a ${cliente.nombre}")
            println("Caja: ${Thread.currentThread().name} precio total de ${cliente.nombre}: $precioTotal €")
        }.also {
            println("Caja: ${Thread.currentThread().name} tiempo de atención final: $it ms")
        }
    }

    override fun call(): Int {
        var precioTotal = 0
        measureTimeMillis {
            println("Caja: ${Thread.currentThread().name} atendiendo a ${cliente.nombre} que tiene ${cliente.carro.productos.size} productos")
            cliente.carro.productos.forEachIndexed { index, producto ->
                println("Caja: ${Thread.currentThread().name} atendiendo a ${cliente.nombre}, producto: ${index + 1} con precio: ${producto.precio} €")
                Thread.sleep(producto.precio.toLong() * 1000)
                precioTotal += producto.precio
            }
            println("Caja: ${Thread.currentThread().name} terminó de atender a ${cliente.nombre}")
            println("Caja: ${Thread.currentThread().name} precio total de ${cliente.nombre}: $precioTotal €")
        }.also {
            println("Caja: ${Thread.currentThread().name} tiempo de atención final: $it ms")
        }
        return precioTotal
    }
}