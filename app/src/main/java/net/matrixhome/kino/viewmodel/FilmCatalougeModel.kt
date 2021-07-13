package net.matrixhome.kino.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.data.Constants
import net.matrixhome.kino.model.FilmRepository
import net.matrixhome.kino.model.GenresRepository
import net.matrixhome.kino.model.Movies
import net.matrixhome.kino.retrofit.Common
import net.matrixhome.kino.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmCatalougeModel : ViewModel() {

    val TAG = "FilmCatalogueModel_log"

    var retrofit: RetrofitService? = null

    private var lastAddedFilmListLiveData = MutableLiveData<ArrayList<Movies>>()//added
    private var byPopularityFilmFilmListsLiveData = MutableLiveData<ArrayList<Movies>>()//rating
    private var byRatingFilmListsLiveData = MutableLiveData<ArrayList<Movies>>()//views
    private var byDatePremiereFilmListsLiveData =
        MutableLiveData<ArrayList<Movies>>()//date_premiere

    private var update_lastAddedFilmList = ArrayList<Movies>()
    private var update_byPopularityFilmFilmLists = ArrayList<Movies>()
    private var update_byRatingFilmLists = ArrayList<Movies>()
    private var update_byDatePremiereFilmLists = ArrayList<Movies>()

    var genreArrayList = MutableLiveData<ArrayList<net.matrixhome.kino.model.Genre>>()

    private var errorDownloadState = MutableLiveData<String>()
    private val coroutineThread = CoroutineScope(Dispatchers.IO)

    var lastAddedItemsCount: Int = 0
    var byPopularityItemsCount: Int = 0
    var byRatingItemsCount: Int = 0
    var byDateItemsCount: Int = 0

    var genreID = MutableLiveData<List<String>>()
    var category = MutableLiveData<List<String>>()
    var countryID = MutableLiveData<String>()
    var yearFromTo = MutableLiveData<String>()
    var offset = MutableLiveData<String>()

    val ADDED = "added"
    val RATING = "rating"
    val VIEWS_MONTH = "views_month"
    val DATE_PREMIERE = "date_premiere"

    var updateListState1 = true
    var updateListState2 = true
    var updateListState4 = true
    var updateListState6 = true

    private var lastAddedOffset: Int = 0
    private var byPopularityOffset: Int = 0
    private var byRatingOffset: Int = 0
    private var byDatePremiereOffset: Int = 0


    init {
        createRetrofit()
        genreID.value = listOf()
        category.value = listOf()
        countryID.value = ""
        yearFromTo.value = ""
        offset.value = ""
        getAllGenres()
        getMovies(lastAddedFilmListLiveData, ADDED, lastAddedOffset, update_lastAddedFilmList)
        getMovies(
            byPopularityFilmFilmListsLiveData,
            VIEWS_MONTH,
            byPopularityOffset,
            update_byPopularityFilmFilmLists
        )
        getMovies(byRatingFilmListsLiveData, RATING, byRatingOffset, update_byRatingFilmLists)
        getMovies(
            byDatePremiereFilmListsLiveData,
            DATE_PREMIERE,
            byDatePremiereOffset,
            update_byDatePremiereFilmLists
        )
    }

    private fun createRetrofit() {
        retrofit = Common.retrofitService
    }


    private fun getMovies(
        liveData: MutableLiveData<ArrayList<Movies>>,
        sortDesc: String,
        offset: Int,
        array: ArrayList<Movies>
    ) {
        Log.d(TAG, "getMovies: genreID " + genreID.value.toString())


        coroutineThread.launch {
            retrofit?.getMoviesSortDesc(
                sortDesc,
                category.value,
                genreID.value,
                countryID.value.toString(),
                yearFromTo.value.toString(),
                Constants.COUNT,
                offset.toString()
            )?.enqueue(object : Callback<FilmRepository> {
                override fun onResponse(
                    call: Call<FilmRepository>,
                    response: Response<FilmRepository>
                ) {
                    Log.d(TAG, "onResponse: " + response.toString())
                    array.addAll(response.body()!!.results)

                    Log.d(TAG, "onResponse: " + array.size)
                    //liveData.postValue(array)
                    liveData.postValue(sortMovieArray(array))
                }

                override fun onFailure(call: Call<FilmRepository>, t: Throwable) {
                    Log.d(TAG, "onFailure: " + t.message)
                    errorDownloadState.postValue(t.message)
                }
            })
        }
    }


    private fun getAllGenres() {
        retrofit?.getGenres()?.enqueue(object : Callback<GenresRepository> {
            override fun onResponse(
                call: Call<GenresRepository>,
                response: Response<GenresRepository>
            ) {
                genreArrayList.postValue(response.body()?.results)
                Log.d(TAG, "onResponse: " + response.body()?.results?.size)
            }

            override fun onFailure(call: Call<GenresRepository>, t: Throwable) {
                errorDownloadState.postValue(t.message)
            }
        })
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



    fun getNewSortedFilList() {
        clearData()
        resetOffset()
        resetUpdateListaState()
        getMovies(lastAddedFilmListLiveData,
            ADDED, lastAddedOffset, update_lastAddedFilmList)
        getMovies(
            byPopularityFilmFilmListsLiveData,
            VIEWS_MONTH,
            byPopularityOffset,
            update_byPopularityFilmFilmLists
        )
        getMovies(byRatingFilmListsLiveData,
            RATING, byRatingOffset, update_byRatingFilmLists)
        getMovies(byDatePremiereFilmListsLiveData,
            DATE_PREMIERE,
            byDatePremiereOffset,
            update_byDatePremiereFilmLists
        )
    }

    fun getByPopList(): MutableLiveData<ArrayList<Movies>> {
        return byPopularityFilmFilmListsLiveData
    }

    fun getLastAddedList(): MutableLiveData<ArrayList<Movies>> {
        return lastAddedFilmListLiveData
    }

    fun getByRatingList(): MutableLiveData<ArrayList<Movies>> {
        return byRatingFilmListsLiveData
    }

    fun getByDatePremiereList(): MutableLiveData<ArrayList<Movies>> {
        return byDatePremiereFilmListsLiveData
    }


    fun updateListByID(id: String) {
        Log.d(TAG, "updateListByID: " + id)
        when (id) {
            ADDED -> {
                lastAddedOffset = lastAddedOffset + Constants.COUNT.toInt()
                getMovies(
                    lastAddedFilmListLiveData,
                    ADDED,
                    lastAddedOffset,
                    update_lastAddedFilmList
                )
            }
            VIEWS_MONTH -> {
                byPopularityOffset = byPopularityOffset + Constants.COUNT.toInt()
                getMovies(
                    byPopularityFilmFilmListsLiveData,
                    VIEWS_MONTH,
                    byPopularityOffset,
                    update_byPopularityFilmFilmLists
                )
            }
            RATING -> {
                byRatingOffset = byRatingOffset + Constants.COUNT.toInt()
                getMovies(
                    byRatingFilmListsLiveData,
                    RATING,
                    byRatingOffset,
                    update_byRatingFilmLists
                )
            }
            DATE_PREMIERE -> {
                byDatePremiereOffset = byDatePremiereOffset + Constants.COUNT.toInt()
                getMovies(
                    byDatePremiereFilmListsLiveData,
                    DATE_PREMIERE,
                    byDatePremiereOffset,
                    update_byDatePremiereFilmLists
                )
            }
        }
    }


    fun clearData() {
        update_byPopularityFilmFilmLists.clear()
        update_byDatePremiereFilmLists.clear()
        update_byRatingFilmLists.clear()
        update_lastAddedFilmList.clear()
    }

    fun resetOffset() {
        lastAddedOffset = 0
        byPopularityOffset = 0
        byRatingOffset = 0
        byDatePremiereOffset = 0
    }

    fun resetUpdateListaState() {
        updateListState1 = true
        updateListState2 = true
        updateListState4 = true
        updateListState6 = true
    }
}
