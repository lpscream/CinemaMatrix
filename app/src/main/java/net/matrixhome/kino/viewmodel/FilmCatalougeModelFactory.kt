package net.matrixhome.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FilmCatalougeModelFactory():ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilmCatalougeModel() as T
    }

}