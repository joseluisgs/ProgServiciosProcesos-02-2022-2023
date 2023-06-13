package dev.joseluisgs.starwarsfx.repositories.pila


interface IPila<T> {
    fun push(dato: T)
    fun pop(): T
    fun peek(): T
    val isEmpty: Boolean
    fun size(): Int
    operator fun get(index: Int): T
    operator fun set(index: Int, value: T)
    fun clear()
    fun toList(): List<T>
}

