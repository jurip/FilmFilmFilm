package api.kudago

import ru.xmn.filmfilmfilm.kudago.data.KudaGoMoviesResponse
import ru.xmn.filmfilmfilm.kudago.data.KudaGoShowingsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class KudaGoManager(val kudaGoClient: KudaGoClient) {
    fun printFilms() {
//        kudaGoClient.getMovies().subscribe({ r -> for (film in r.results) println(film.title) })
    }
}

class KudaGoClient() {

//    fun getMovieShowings(): Observable<KudaGoShowingsResponse> {
//        val since = Date().time / 1000
//        val until = since + 3600 * 24
//        return client.getMovieShowings("msk", "movie,place", 100, since, until)
//    }
//
//    fun getMovies(): Observable<KudaGoMoviesResponse> {
//        val since = Date().time / 1000
//        val until = since + 3600 * 24
//        return client.getMovies("msk", "movie,place", 100, since, until)
//    }
}

interface KudaGoApi {
    @GET("movie-showings/")
    fun getMovieShowings(
            @Query("location") location: String,
            @Query("expand") expand: String,
            @Query("page_size") pageSize: Int,
            @Query("actual_since") actualSince: Long,
            @Query("actual_until") actualUntil: Long)
            : Observable<KudaGoShowingsResponse>

    @GET("movies/")
    fun getMovies(
            @Query("location") location: String,
            @Query("expand") expand: String,
            @Query("page_size") pageSize: Int,
            @Query("actual_since") actualSince: Long,
            @Query("actual_until") actualUntil: Long)
            : Observable<KudaGoMoviesResponse>
}
