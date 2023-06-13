package dev.joseluisgs.starwarsfx.repositories.matrix


class Matrix<T>(override var rows: Int, override var cols: Int) : IMatrix<T> {
    private val matrix: MutableList<MutableList<T?>> = mutableListOf()

    init {
        matrix.clear()
        for (i in 0 until rows) {
            val fila = MutableList<T?>(cols) { null }
            for (j in 0 until cols) {
                fila.add(null)
            }
            matrix.add(fila)
        }
    }

    override fun get(row: Int, col: Int): T? {
        return matrix[row][col]
    }

    override fun set(row: Int, col: Int, value: T?) {
        matrix[row][col] = value
    }

    override fun clear() {
        // La inicializamos a null creando los arrayList
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                this[i, j] = null
            }
        }
    }

    override fun addRows(numberOfRows: Int) {
        for (i in 0 until numberOfRows) {
            // Creamos por cada fila, la columnas a null
            val fila = MutableList<T?>(cols) { null }
            for (j in 0 until cols) {
                fila.add(null)
            }
            matrix.add(fila)
        }
        rows += numberOfRows
    }

    override fun addCols(numberOfCols: Int) {
        for (i in 0 until rows) {
            for (j in 0 until numberOfCols) {
                matrix[i].add(null)
            }
        }
        cols += numberOfCols
    }

    override fun removeRows(numberOfRows: Int) {
        for (i in 0 until numberOfRows) {
            matrix.removeAt(0)
        }
        rows -= numberOfRows
    }

    override fun removeCols(numberOfCols: Int) {
        for (i in 0 until rows) {
            for (j in 0 until numberOfCols) {
                matrix[i].removeAt(matrix[i].size - 1)
            }
        }
        cols -= numberOfCols
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (this[i, j] != null) {
                    sb.append("[").append(this[i, j]).append("]")
                } else {
                    sb.append("[ ]")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

