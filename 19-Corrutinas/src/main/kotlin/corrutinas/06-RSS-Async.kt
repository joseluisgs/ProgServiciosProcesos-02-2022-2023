package corrutinas

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.system.measureTimeMillis

data class Noticia(
    var titulo: String = "",
    var link: String = "",
    var descripcion: String = "",
    var autor: String = "",
    var contenido: String = "",
    var fecha: String = "",
    var imagen: String = ""
)

private val logger = KotlinLogging.logger {}

private suspend fun getNoticias(uri: String): MutableList<Noticia> = withContext(Dispatchers.IO) {
    // Parser de XMl, recorremos el DOM
    val factory = DocumentBuilderFactory.newInstance()
    // Lista de noticias, mutable
    val noticias = mutableListOf<Noticia>()

    try {
        // Filtramos por elementos del RSS
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(uri)
        val items = document.getElementsByTagName("item")

        // Recorremos los elementos
        for (i in 0 until items.length) {
            val nodo = items.item(i)
            val noticia = Noticia()
            // Vamos a contar las imagenes que hay
            var contadorImagenes = 0
            var n = nodo.firstChild
            while (n != null) {
                when (n.nodeName) {
                    "title" -> {
                        noticia.titulo = n.textContent
                    }

                    "link" -> {
                        noticia.link = n.textContent
                    }

                    "description" -> {
                        noticia.descripcion = n.textContent
                    }

                    "pubDate" -> {
                        noticia.fecha = n.textContent
                    }

                    "dc:creator" -> {
                        noticia.autor = n.textContent
                    }

                    "content:encoded" -> {
                        noticia.contenido = n.textContent
                    }

                    "enclosure" -> {
                        val imagen: String = (n as Element).getAttribute("url")
                        //Controlamos que solo rescate una imagen
                        if (contadorImagenes == 0) {
                            noticia.imagen = imagen
                        }
                        contadorImagenes++
                    }
                }
                // Vamos al siguiente nodo
                n = n.nextSibling
            }
            noticias.add(noticia)
        }
        // Log.d("Noticias", "Noticias Controller tam: " + noticias.size.toString())
        return@withContext noticias
    } catch (e: ParserConfigurationException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: IOException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: DOMException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: SAXException) {
        logger.debug("Noticias", "Error: " + e.message)
    }
    return@withContext noticias
}

fun main() = runBlocking<Unit> {

    measureTimeMillis {
        println("Obteniendo noticias")
        val noticiasAsync = async { getNoticias("https://www.20minutos.es/rss/") }
        print("Descargando:")
        // Esto es para que se quede bonito, pero ene l fondo me esta suspendiendo con el delay!!!
        while (!noticiasAsync.isCompleted) {
            print(".")
            delay(250)
        }
        println()
        println("Noticias descargadas")
        val noticias = noticiasAsync.await()
        println("Noticias obtenidas: ${noticias.size}")

        noticias.forEachIndexed { index, noticia ->
            println("Nº ${index + 1}: $noticia")
        }

        // Otra forma
        println("Obteniendo noticias 2 ")
        val noticias2 = withTimeoutOrNull(10) {
            getNoticias("https://www.20minutos.es/rss/")
        }
        noticias2?.let {
            println("Noticias obtenidas: ${it.size}")
            noticias2.forEachIndexed { index, noticia ->
                println("Nº ${index + 1}: $noticia")
            }
        } ?: run { println("No se han podido obtener las noticias") }

    }.also { println("Tiempo de ejecución: $it ms") }
}
