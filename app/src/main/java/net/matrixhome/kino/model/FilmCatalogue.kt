package net.matrixhome.kino.model

data class FilmCatalogue(
       var id:String,
       var name:String,
       var description:String,
       var time:String,
       var director:String,
       var actors:String,
       var added: Int,
       var year:String,
       var hd:Int,
       var kinopoisk_id:String,
       var age:String,
       var country:String,
       var date_premiere:String,
       var translate_id:String,
       var serial_id:String,
       var serial_count_seasons:String,
       var season_number:String,
       var serial_name:String,
       var serial_o_name:String,
       var vote_percent:String,
       var rating_vote_avg:String,
       var made:String,
       var count_torrents:String,
       var translate:String,
       var genres:String,
       var cover:String,
       var category:String,
       var original_name:String,
       var rating:String,
       var views:String,
       var views_month:String,
       var mpaa:String,
       var serial_views:String,
       var video_views:String,
       //var trailer_urls:ArrayList<String>,
       var url:String,
       var cover_200:String,
       //var series:ArrayList<String>
) {
    override fun toString(): String {
        return super.toString()
    }
}
