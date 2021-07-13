package net.matrixhome.kino.viewmodel

import android.util.Log
import androidx.lifecycle.*
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

class SearchViewModel() : ViewModel() {
    private val searchFilmList =  MutableLiveData<ArrayList<Movies>>()




    private var  retrofit: RetrofitService? = null
    private val coroutineThread = CoroutineScope(Dispatchers.IO)
    private val TAG = "SearchViewModel_log"



    init {
        retrofit = Common.retrofitService

    }




    fun getSearchResult(): LiveData<ArrayList<Movies>>{
        return searchFilmList
    }


    fun getSearchResult(query: String) {
        coroutineThread.launch {
            retrofit?.searchMovies("added", query, Constants.KEY)?.enqueue(object: Callback<FilmRepository>{
                override fun onResponse(
                    call: Call<FilmRepository>,
                    response: Response<FilmRepository>
                ) {
                    searchFilmList.postValue(sortMovieArray(response.body()?.results!!))
                }

                override fun onFailure(call: Call<FilmRepository>, t: Throwable) {
                    Log.d(TAG, "onFailure: " + call.request())
                    Log.d(TAG, "onResponse: " + call.toString())
                    Log.d(TAG, "onFailure: " + t.message)
                }
            })
        }

    }


    private fun sortMovieArray(array: ArrayList<Movies>): ArrayList<Movies> {
        var str: String = ""
        var str2: String = ""
        var arrayLIst = array as List<Movies>
        for (i in array.indices) {
            if (i < array.size){
                if (array[i].serial_id != null){
                    var j: Int = 1
                    while (j <= array.size){
                        if (i < array.size && j < array.size){
                            if (array[j].serial_id != null){
                                if (array[i].serial_id == array[j].serial_id){
                                    if (j != i){
                                        array.removeAt(i)
                                        if (j != 0){
                                            j = j - 1
                                        }
                                    }
                                }
                            }
                        }
                        j++
                    }
                }

            }
        }
        return array
    }
}