import corrutinas.Noticia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.xml.sax.SAXException
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.system.measureTimeMillis


private val logger = KotlinLogging.logger {}

// Las secuencias no pueden ser funciones de suspensión tan sencillamente,
// Prueba tú a ver si lo consigues :)
private fun getNoticiasSequence(uri: String): Flow<List<Noticia>> = flow {
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
        // Emitimos la noticia, de hecho iremos emitiendo las noticias mientras nos pidan
        // Para hacerlo con vidilla vamos a suponer que estamos haciendo una llamada a una API
        /// y que recibimos las noticias
        while (true) {
            log("Emitiendo noticias")
            emit(noticias.shuffled())
            delay(5000)
        }

        // Log.d("Noticias", "Noticias Controller tam: " + noticias.size.toString())
    } catch (e: ParserConfigurationException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: IOException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: DOMException) {
        logger.debug("Noticias", "Error: " + e.message)
    } catch (e: SAXException) {
        logger.debug("Noticias", "Error: " + e.message)
    }
}.flowOn(Dispatchers.IO) // Si queremos que se ejecute en un Dispacher

// Hacerlo con secuencias no es una buena idea...
fun main() = runBlocking<Unit> {

    measureTimeMillis {
        println("Obteniendo noticias")
        val noticiasFlow = getNoticiasSequence("https://www.20minutos.es/rss/")


        launch {
            // Un cliente!! Se va a quedar fijo recogiendo!!
            delay(3000)
            noticiasFlow.collect {
                println("Actualización de noticias Cliente 1")
                println("Noticias Cliente 1: ${it.size}")
                it.distinct().take(10).forEachIndexed { index, noticia ->
                    println("Cliente 1 -> Noticia ${index + 1}: ${noticia.titulo}")
                }
            }
        }

        // obtenemos las 10 primeras noticias
        launch {
            // Otro cliente!!
            noticiasFlow.take(10).collect {
                println("Actualización de noticias Cliente 2")
                println("Noticias Cliente 2: ${it.size}")
                it.take(10).forEachIndexed { index, noticia ->
                    println("Cliente 2 -> Noticia ${index + 1}: ${noticia.titulo}")
                }
            }
            log("Terminado Cliente 2")
        }

    }.also { println("Tiempo de ejecución: $it ms") }
}
