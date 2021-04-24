package net.matrixhome.kino.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.matrixhome.kino.data.FilmList
import net.matrixhome.kino.model.FilmCatalogue

class MainViewModelFactory(val application: Application):ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            return FilmViewModel(application) as T

    }
}