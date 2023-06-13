package flows

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import log
import okhttp3.MediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.system.exitProcess

@Serializable
data class User(
    val id: Int = 0,
    var name: String,
    var username: String,
    var email: String,
    var address: Address? = null,
    var phone: String?,
    var website: String?,
    var company: Company? = null
) {
    @Serializable
    data class Address(
        var street: String,
        var suite: String,
        var city: String,
        var zipcode: String,
        var geo: Geo
    )

    @Serializable
    data class Company(
        var name: String,
        var catchPhrase: String,
        var bs: String
    )

    @Serializable
    data class Geo(
        var lat: Double,
        var lng: Double
    )
}

/**
 * Voy a usar funciones suspendidas, para poder usar código asíncrono. Si no quitar el suspend y cambiar Response<T>
 * por Call<T>
 * Aqí van todas las llamadas a la API REST que quiera hacer
 */
/**
 * Voy a usar funciones suspendidas, para poder usar código asíncrono. Si no quitar el suspend y cambiar Response<T>
 * por Call<T>
 * Aqí van todas las llamadas a la API REST que quiera hacer
 */
interface PlaceHolderRest {

    @GET("/users")
    suspend fun getAll(): Response<List<User>>

    @GET("/users/{id}")
    suspend fun getById(@Path("id") id: Int): Response<User>
}

/**
 * Cliente de nuestra API REST
 */
object PlaceHolder {
    private const val API_URL = "https://jsonplaceholder.typicode.com/"

    // Creamos una instancia de Retrofit con las llamadas a la API
    @OptIn(ExperimentalSerializationApi::class)
    fun api(): PlaceHolderRest {
        val contentType = MediaType.get("application/json")
        return Retrofit.Builder().baseUrl(API_URL)
            // Nuestro conversor de JSON
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(PlaceHolderRest::class.java)
    }
}

suspend fun getAllFlow(): Flow<User?> {
    println("GET /users -> getAll as Flow")
    val restClient = PlaceHolder.api()

    return flow {
        while (true) {
            log("Llamando a getAll desde el getAllFlow")
            val response = restClient.getAll()
            if (response.isSuccessful) {
                val data = response.body()
                data?.forEach {
                    // Podemos emeitirlos de uno en uno o todos a la vez
                    emit(it)
                }
            } else {
                emit(null)
            }
            delay(5000)
        }
    }.flowOn(Dispatchers.IO)
}

/**
 * Voy a hacerlo con un flujo de datos simplificado
 */
suspend fun getAllFlowSimple(): Flow<User?> {
    println("GET /users -> getAll as Flow Simple")
    return PlaceHolder.api().getAll().body()?.asFlow() ?: flowOf(null).flowOn(Dispatchers.IO)
}

/**
 * Ejemplo de uso de Retrofit
 */
fun main() = runBlocking<Unit> {
    println("Ejemplo de uso de Retrofit")

    var count = 1

    val job = launch {
        println("Cliente GET /users -> getAll")
        getAllFlow().collect {
            println("Actualizado Cliente 1 $count")
            println("Cliente 1 -> $it")
            count++ // Recuerda que recibes de 1 en 1
        }
    }

    launch {
        println("Cliente GET /users -> getAll Simple")
        getAllFlowSimple().distinctUntilChanged()
            .filter { it?.username?.lowercase()?.startsWith("a") == true }
            .take(3)
            .collect { println("Cliente 2 -> $it") }
    }

    while (count < 50) {
        println("Esperando a que se actualice el cliente 1")
        delay(1000)
    }
    println("Cancelando el cliente 1 para salir")
    job.cancel()
    println("Fin")
    exitProcess(0)

}

