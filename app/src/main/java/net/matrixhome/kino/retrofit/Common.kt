package net.matrixhome.kino.retrofit

object Common {
    private val BASE_URL = "http://iptv.matrixhome.net/api/video/"
    val retrofitService: RetrofitService get() = RetrofitClient
            .getClient(BASE_URL)
            .create(RetrofitService::class.java)
}