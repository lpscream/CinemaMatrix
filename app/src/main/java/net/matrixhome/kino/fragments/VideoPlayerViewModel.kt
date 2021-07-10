package net.matrixhome.kino.fragments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.data.Constants
import net.matrixhome.kino.model.FilmRepository
import net.matrixhome.kino.model.Movies
import net.matrixhome.kino.model.VideoLinkRepository
import net.matrixhome.kino.retrofit.Common
import net.matrixhome.kino.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoPlayerViewModel : ViewModel() {
    private var filmRepository: MutableLiveData<Movies> = MutableLiveData<Movies>()
    private var linksRepo: MutableLiveData<ArrayList<String>> = MutableLiveData<ArrayList<String>>()
    private var links : ArrayList<String> = arrayListOf()
    private var retrofit: RetrofitService? = null
    private val coroutineThread = CoroutineScope(Dispatchers.IO)
    private val TAG = "VideoPlrViewModel_log"

    init {
        createRetrofit()
    }

    fun getRepositoryByID(id: String){
        coroutineThread.launch {
            retrofit?.getFilmByID(id, Constants.KEY)?.enqueue(object : Callback<FilmRepository> {
                override fun onResponse(
                    call: Call<FilmRepository>,
                    response: Response<FilmRepository>
                ) {
                    filmRepository.postValue(response.body()?.results?.get(0))
                }

                override fun onFailure(call: Call<FilmRepository>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun initLinks(){
        var num: Int = filmRepository.value!!.series.size - 1
        Log.d(TAG, "initLinks: num is " + num)
        Log.d(TAG, "initLinks: series.size " + filmRepository.value!!.series.size)
        coroutineThread.launch {
            for (i in filmRepository.value!!.series.indices){
                retrofit?.getVideoLink(filmRepository.value!!.id, filmRepository.value!!.series.get(i))?.
                enqueue(object : Callback<VideoLinkRepository>{
                    override fun onResponse(
                        call: Call<VideoLinkRepository>,
                        response: Response<VideoLinkRepository>
                    ) {
                        links.add("https:" + response.body()?.results.toString())
                        Log.d(TAG, "onResponse: link.size " + links.size)
                        //TODO надо чтобы заполнялось одним заходом

                            linksRepo.postValue(links)
                            Log.d(TAG, "onResponse: links.size " + links.size)

                    }

                    override fun onFailure(call: Call<VideoLinkRepository>, t: Throwable) {
                        Log.d(TAG, "onFailure: " + t.message)
                        Log.d(TAG, "onFailure: " + t.stackTrace)
                    }


                })
            }
        }
    }

    private fun createRetrofit() {
        retrofit = Common.retrofitService
    }

    fun getFilmRepository(): MutableLiveData<Movies>{
        return filmRepository
    }

    fun getLinksRepo(): MutableLiveData<ArrayList<String>>{
        return linksRepo
    }


}