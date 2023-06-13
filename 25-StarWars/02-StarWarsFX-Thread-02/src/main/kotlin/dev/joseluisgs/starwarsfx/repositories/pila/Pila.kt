package dev.joseluisgs.starwarsfx.repositories.pila


class Pila<T> : IPila<T> {
    private val pila: ArrayDeque<T> = ArrayDeque()

    override fun push(dato: T) {
        pila.add(dato)
    }

    override fun pop(): T {
        return pila.removeLast()
    }

    override fun peek(): T {
        return pila.last()
    }

    override val isEmpty: Boolean
        get() = pila.isEmpty()

    override fun size(): Int {
        return pila.size
    }

    override fun get(index: Int): T {
        return pila.elementAt(index)
    }

    override fun clear() {
        pila.clear()
    }

    override fun toList(): List<T> {
        return pila.toList()
    }

    override fun set(index: Int, value: T) {
        pila.add(index, value)
    }
}

