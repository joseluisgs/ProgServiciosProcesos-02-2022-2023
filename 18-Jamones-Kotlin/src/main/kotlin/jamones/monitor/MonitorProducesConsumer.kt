package es.joseluisgs.dam.jamones.monitor

interface MonitorProducerConsumer<T> {
    fun get(): T
    fun put(item: T)
}