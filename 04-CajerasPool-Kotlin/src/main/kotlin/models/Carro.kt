package models

class Carro {
    private var _productos = mutableListOf<Producto>()
    val productos: List<Producto>
        get() = _productos.toList()

    init {
        repeat((5..15).random()) {
            _productos.add(Producto())
        }
    }

    override fun toString(): String {
        return productos.toString()
    }
}