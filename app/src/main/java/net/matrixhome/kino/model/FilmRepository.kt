package net.matrixhome.kino.model

import android.util.ArrayMap
import java.io.Serializable

data class FilmRepository(val results: ArrayList<Movies>)


data class Movies(
        var id: String,
        var name: String,
        var description: String,
        var time: String,
        var director: String,
        var actors: String,
        var added: Int,
        var year: String,
        var hd: Int,
        var kinopoisk_id: String,
        var age: String,
        var country: String,
        var date_premiere: String,
        var translate_id: String,
        var serial_id: String,
        var serial_count_seasons: String,
        var season_number: String,
        var serial_name: String,
        var serial_o_name: String,
        var vote_percent: String,
        var rating_vote_avg: String,
        var made: String,
        var count_torrents: String,
        var translate: String,
        var genres: String,
        var genres_ids: ArrayList<String>,
        var cover: String,
        var category: String,
        var original_name: String,
        var rating: String,
        var views: String,
        var views_month: String,
        var mpaa: String,
        var serial_views: String,
        var video_views: String,
        var series: ArrayList<String> = arrayListOf(),
        var url: String,
        var cover_200: String,
        var frames: ArrayList<String>,
        var tags: ArrayList<String>,
        var vote_count: VoteCount,
        var serial_vote_count: SerialVoteCount,
        var rating_vote_count: RatingVoteCount,
        var rating_kinopoisk: RatingKinopoisk,
        var rating_imdb: RatingIMDB,
        var trailer_urls: ArrayList<String>
) : Serializable

data class VoteCount(var count: String,
                     var count_like: String,
                     var count_dislike: String,
                     var count_neutral: String) : Serializable

data class SerialVoteCount(var count: String,
                           var count_like: String,
                           var count_dislike: String,
                           var count_neutral: String) : Serializable

data class RatingVoteCount(var count: String,
                           var sum: String,
                           var value_count: ArrayMap<String, String>) : Serializable

data class RatingKinopoisk(var id: String,
                           var rating: String,
                           var count: String) : Serializable

data class RatingIMDB(var id: String,
                      var rating: String,
                      var count: String) : Serializable