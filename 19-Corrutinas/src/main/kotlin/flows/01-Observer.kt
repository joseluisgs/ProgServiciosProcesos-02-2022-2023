import kotlin.properties.Delegates

/**
 * Patron Observer
 * Consiste en que un objeto notifica a otros objetos de los cambios que se producen en el.
 * Es la base de la programación reactiva
 * https://refactoring.guru/es/design-patterns/observer
 */
// Una interfaz, podríamos usar genéricos
interface Publisher {
    fun onNews(news: String)
}

// Radio Una clase que implementa la interfaz
class RadioChannel : Publisher {
    override fun onNews(news: String) = println("La radio informa: $news")
}

// Periodico Una clase que implementa la interfaz
class Newspaper : Publisher {
    override fun onNews(news: String) = println("El periódico informa: $news")
}

// Agencia que es observada
class NewsAgency {
    // Lista de observadores, son los que implementan la interfaz
    private val listeners = mutableListOf<Publisher>()

    // Usamos los delegados que automaticamente si detectan un cambio avisan
    var news: String by Delegates.observable(initialValue = "") { _, old, new ->
        if (new != old) listeners.map { listener -> listener.onNews(new) }
    }

    // Añadimos un observador
    fun subscribe(publisher: Publisher) = listeners.add(publisher)

    // Eliminamos un observador
    fun unsubscribe(publisher: Publisher) = listeners.remove(publisher)
}

fun main() {
    // Preparamos los objetos
    val radioChannel = RadioChannel()
    val newspaper = Newspaper()
    val newsAgency = NewsAgency()

    // Suscribimos a la agencia, me observan
    newsAgency.subscribe(radioChannel)
    newsAgency.subscribe(newspaper)

    // Lanzamos una noticia. Al estar el delegado, la observa y automaticamente notifica
    // A mis observadores
    newsAgency.news = "¡Nadal Gana!"
    newsAgency.news = "¡Hoy llueve!"
    newsAgency.news = "¡Todos han aprobado!"

    // Los periódicos se retiran
    newsAgency.unsubscribe(newspaper)
    newsAgency.news = "Llegan las vacaciones :)"
}