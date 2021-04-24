package net.matrixhome.kino.data

import java.io.Serializable

class FilmDescription: Serializable {
    lateinit var id:String
    lateinit var name:String
    lateinit var description:String
    lateinit var time:String
    lateinit var director:String
    lateinit var actors:String
    var added:Int = 0
    lateinit var year:String
    var hd:Int = 0
    lateinit var kinopoisk_id:String
    lateinit var age:String
    lateinit var country:String
    lateinit var date_premiere:String
    lateinit var translate_id:String
    lateinit var serial_id:String
    lateinit var serial_count_seasons:String
    lateinit var season_number:String
    lateinit var serial_name:String
    lateinit var serial_o_name:String
    lateinit var vote_percent:String
    lateinit var rating_vote_avg:String
    lateinit var made:String
    lateinit var count_torrents:String
    lateinit var translate:String
    lateinit var genres:String
    lateinit var cover:String
    lateinit var category:String
    lateinit var original_name:String
    lateinit var rating:String
    lateinit var views:String
    lateinit var views_month:String
    lateinit var mpaa:String
    lateinit var serial_views:String
    lateinit var video_views:String
    lateinit var trailer_urls:ArrayList<String>
    lateinit var url:String
    lateinit var cover_200:String
    lateinit var series:ArrayList<String>

}