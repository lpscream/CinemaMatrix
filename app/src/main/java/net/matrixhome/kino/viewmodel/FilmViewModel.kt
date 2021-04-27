package net.matrixhome.kino.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.matrixhome.kino.data.Constants
import net.matrixhome.kino.data.FilmList
import net.matrixhome.kino.data.Genre
import net.matrixhome.kino.data.ViewModelDataDownloader

class FilmViewModel(application: Application) : AndroidViewModel(application) {
    //film database
    var lastAddedFilmList = MutableLiveData<ArrayList<FilmList>>()
    var byPopularityFilmFilmLists = MutableLiveData<ArrayList<FilmList>>()
    var byViewsFilmLists = MutableLiveData<ArrayList<FilmList>>()
    var byRatingFilmLists = MutableLiveData<ArrayList<FilmList>>()
    var byNameFilmLists = MutableLiveData<ArrayList<FilmList>>()
    var byDatePremiereFilmLists = MutableLiveData<ArrayList<FilmList>>()
    var estimatedFilmLists = MutableLiveData<ArrayList<FilmList>>()

    var genreID = MutableLiveData<String>()
    var countryID = MutableLiveData<String>()
    var yearFromTo = MutableLiveData<String>()
    var category = MutableLiveData<String>()
    var query = MutableLiveData<String>()
    var position = MutableLiveData<Int>()

    private var update_lastAddedFilmList = ArrayList<FilmList>()
    private var update_byPopularityFilmFilmLists = ArrayList<FilmList>()
    private var update_byViewsFilmLists = ArrayList<FilmList>()
    private var update_byRatingFilmLists = ArrayList<FilmList>()
    private var update_byNameFilmLists = ArrayList<FilmList>()
    private var update_byDatePremiereFilmLists = ArrayList<FilmList>()
    private var update_estimatedFilmLists = ArrayList<FilmList>()

    var updatelastAddedFilmList = ArrayList<FilmList>()
    var newFilmList = MutableLiveData<ArrayList<FilmList>>()

    var updateListState1 = true
    var updateListState2 = true
    var updateListState3 = true
    var updateListState4 = true
    var updateListState5 = true
    var updateListState6 = true
    var updateListState7 = true

    var lastAddedItemsCount: Int = 0
    var byPopularityItemsCount: Int = 0
    var byViewsItemsCount: Int = 0
    var byRatingItemsCount: Int = 0
    var byNameItemsCount: Int = 0
    var byDateItemsCount: Int = 0
    var estimatedItemsCount: Int = 0

    private var lastAddedOffset: Int = 0
    private var byPopularityOffset: Int = 0
    private var byViewsOffset: Int = 0
    private var byRatingOffset: Int = 0
    private var byNameOffset: Int = 0
    private var byDatePremiereOffset: Int = 0
    private var estimatedOffset: Int = 0


    val ACTION_VIDEO = "?action=video"

    val SORT_DESC_ADDED = "&sort_desc=added"
    val SORT_DESC_VIEWS_MONTH = "&sort_desc=views_month"
    val SORT_DESC_VIEWS = "&sort_desc=views"
    val SORT_DESC_RATING = "&sort_desc=rating"
    val SORT_DESC_NAME = "&sort_desc=name"
    val SORT_DESC_DATE_PREMIERE = "&sort_desc=date_premiere"
    val SORT_DESC_RATING_VOTE = "&sort_desc=rating_vote"
    val LIMIT = "&limit="


    var genreArrayList = MutableLiveData<ArrayList<Genre>>()
    var genreLink = "?action=genre"

    val LASTADDED_ID: Int = 0
    val BYPOPULARITY_ID: Int = 1
    val BYVIEW_ID: Int = 2
    val BYRATING_ID: Int = 3
    val BYNAME_ID: Int = 4
    val BYDATEPREMIER_ID: Int = 5
    val ESTIMATED_ID: Int = 6


    var lastAppVersion = MutableLiveData<Int>()

    //array to add new film list to database
    val TAG: String = "FilmViewModel"
    val viewModelDataDownloader: ViewModelDataDownloader = ViewModelDataDownloader()
    val coroutineThread = CoroutineScope(Dispatchers.IO)


    override fun onCleared() {
        super.onCleared()
    }

    init {



        genreID.value = ""
        category.value = ""
        countryID.value = ""
        yearFromTo.value = ""

        coroutineThread.launch {
            genreArrayList.postValue(viewModelDataDownloader.getAllGenres(genreLink))
        }

        getAllDataOnStart()
    }

    public fun getNewSortedFilList(){
        clearAllData()
        resetOffset()
        resetUpdateListaState()
        getAllDataOnStart()
    }

    fun updateFilmList(host: String, id: Int) {
        when (id) {
            LASTADDED_ID -> {
                lastAddedOffset = lastAddedOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_lastAddedFilmList.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + lastAddedOffset.toString()))
                    lastAddedFilmList.postValue(update_lastAddedFilmList)
                }
            }
            BYPOPULARITY_ID -> {
                byPopularityOffset = byPopularityOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_byPopularityFilmFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + byPopularityOffset.toString()))
                    byPopularityFilmFilmLists.postValue(update_byPopularityFilmFilmLists)
                }
            }
            BYVIEW_ID -> {
                byViewsOffset = byViewsOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_byViewsFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + byViewsOffset.toString()))
                    byViewsFilmLists.postValue(update_byViewsFilmLists)
                }
            }
            BYRATING_ID -> {
                byRatingOffset = byRatingOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_byRatingFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + byRatingOffset.toString()))
                    byRatingFilmLists.postValue(update_byRatingFilmLists)
                }
            }
            BYNAME_ID -> {
                byNameOffset = byNameOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_byNameFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + byNameOffset.toString()))
                    byNameFilmLists.postValue(update_byNameFilmLists)
                }
            }
            BYDATEPREMIER_ID -> {
                byDatePremiereOffset = byDatePremiereOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_byDatePremiereFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + byDatePremiereOffset.toString()))
                    byDatePremiereFilmLists.postValue(update_byDatePremiereFilmLists)
                }
            }
            ESTIMATED_ID -> {
                estimatedOffset = estimatedOffset + Constants.COUNT.toInt()
                coroutineThread.launch {
                    update_estimatedFilmLists.addAll(viewModelDataDownloader.getAllData(host + Constants.COUNT + "&offset=" + estimatedOffset.toString()))
                    estimatedFilmLists.postValue(update_estimatedFilmLists)
                }
            }
        }
    }

    private fun getAllDataOnStart() {
        coroutineThread.launch {
            update_lastAddedFilmList.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_ADDED
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            lastAddedFilmList.postValue(update_lastAddedFilmList)
        }
        coroutineThread.launch {
            update_byPopularityFilmFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_VIEWS_MONTH
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            byPopularityFilmFilmLists.postValue(update_byPopularityFilmFilmLists)
        }
        coroutineThread.launch {
            update_byViewsFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_VIEWS
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            byViewsFilmLists.postValue(update_byViewsFilmLists)
        }
        coroutineThread.launch {
            update_byRatingFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_RATING
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            byRatingFilmLists.postValue(update_byRatingFilmLists)
        }
        coroutineThread.launch {
            update_byNameFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_NAME
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            byNameFilmLists.postValue(update_byNameFilmLists)
        }
        coroutineThread.launch {
            update_byDatePremiereFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_DATE_PREMIERE
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            byDatePremiereFilmLists.postValue(update_byDatePremiereFilmLists)
        }
        coroutineThread.launch {
            update_estimatedFilmLists.addAll(viewModelDataDownloader.getAllData(
                    ACTION_VIDEO
                    + SORT_DESC_RATING_VOTE
                    + category.value
                    + genreID.value
                    + countryID.value
                    + yearFromTo.value
                    + LIMIT
                    + Constants.COUNT))
            estimatedFilmLists.postValue(update_estimatedFilmLists)
        }
    }

    private fun clearAllData(){
        update_lastAddedFilmList.clear()
        update_byPopularityFilmFilmLists.clear()
        update_byViewsFilmLists.clear()
        update_byRatingFilmLists.clear()
        update_byNameFilmLists.clear()
        update_byDatePremiereFilmLists.clear()
        update_estimatedFilmLists.clear()

        lastAddedFilmList.value = update_lastAddedFilmList
        byPopularityFilmFilmLists.value = update_byPopularityFilmFilmLists
        byViewsFilmLists.value = update_byViewsFilmLists
        byRatingFilmLists.value = update_byRatingFilmLists
        byNameFilmLists.value = update_byNameFilmLists
        byDatePremiereFilmLists.value = update_byDatePremiereFilmLists
        estimatedFilmLists.value = update_estimatedFilmLists
    }

    private fun resetOffset(){
        lastAddedOffset = 0
        byPopularityOffset = 0
        byViewsOffset = 0
        byRatingOffset = 0
        byNameOffset = 0
        byDatePremiereOffset = 0
        estimatedOffset = 0
    }

    fun resetUpdateListaState(){
        updateListState1 = true
        updateListState2 = true
        updateListState3 = true
        updateListState4 = true
        updateListState5 = true
        updateListState6 = true
        updateListState7 = true
    }


}




