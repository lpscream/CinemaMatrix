package net.matrixhome.kino.retrofit

import net.matrixhome.kino.data.Constants
import net.matrixhome.kino.model.FilmRepository
import net.matrixhome.kino.model.GenresRepository
import net.matrixhome.kino.model.VideoLinkRepository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {


    @Headers("Accept: application/json")
    @GET("?action=video")
    fun getMoviesSortDesc(
        @Query("sort_desc") sort_desc: String,
        @Query("category[]") category: List<String>?,
        @Query("genre_id[]") genre_id: List<String>?,
        @Query("made") made: String,
        @Query("year[]") year: String,
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<FilmRepository>


    @Headers("Accept: application/json")
    @GET("?action=video")
    fun getMoviesSort(
        @Query("sort") sort_desc: String,
        @Query("category[]") category: List<String>,
        @Query("genre_id[]") genre_id: List<String>?,
        @Query("made") made: String,
        @Query("year[]") year: String,
        @Query("limit") limit: String,
        @Query("offset") offset: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<FilmRepository>


    @Headers("Accept: application/json")
    @GET("?action=video")
    fun getSerialByID(
        @Query("serial_id") serial_id: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<FilmRepository>


    @Headers("Accept: application/json")
    @GET("?action=video")
    fun getFilmByID(
        @Query("video_id") video_id: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<FilmRepository>


    @Headers("Accept: application/json")
    @GET("?action=genre")
    fun getGenres(
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<GenresRepository>


    @Headers("Accept: application/json")
    @GET("?action=video")
    fun searchMovies(
        @Query("sortby") sort_by: String,
        @Query("q") query: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<FilmRepository>


    @Headers("Accept: application/json")
    @GET("?action=link")
    fun getVideoLink(
        @Query("video_id") video_id: String,
        @Query("episode") episode: String,
        @Query("api_key") api_key: String = Constants.KEY
    ): Call<VideoLinkRepository>
}