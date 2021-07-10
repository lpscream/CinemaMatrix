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
import net.matrixhome.kino.retrofit.Common
import net.matrixhome.kino.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DescriptionFilmViewModel : ViewModel() {
    private var filmRepository: MutableLiveData<Movies> = MutableLiveData<Movies>()
    private var serialRepository: MutableLiveData<ArrayList<Movies>> = MutableLiveData<ArrayList<Movies>>()
    private var retrofit: RetrofitService? = null
    private val coroutineThread = CoroutineScope(Dispatchers.IO)
    private val TAG = "DescriptionFilmVM_log"

    init {
        createRetrofit()
    }

    fun getRepositoryByID(id: String){
        coroutineThread.launch {
            retrofit?.getFilmByID(id, Constants.KEY)?.enqueue(object : Callback<FilmRepository>{
                override fun onResponse(
                    call: Call<FilmRepository>,
                    response: Response<FilmRepository>
                ) {
                    filmRepository.postValue(response.body()?.results?.get(0))
                    Log.d(TAG, "onResponse: size " + response.body()?.results?.size)
                }

                override fun onFailure(call: Call<FilmRepository>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun getRepositoryBySerialID(serial_id: String){
        retrofit?.getSerialByID(serial_id, Constants.KEY)?.enqueue(object :Callback<FilmRepository>{
            override fun onResponse(
                call: Call<FilmRepository>,
                response: Response<FilmRepository>
            ) {
                serialRepository.postValue(response.body()?.results)
            }

            override fun onFailure(call: Call<FilmRepository>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun createRetrofit() {
        retrofit = Common.retrofitService
    }

    fun getFilmRepository(): MutableLiveData<Movies>{
        return filmRepository
    }

    fun getSerialRepository(): MutableLiveData<ArrayList<Movies>>{
        return serialRepository
    }
}