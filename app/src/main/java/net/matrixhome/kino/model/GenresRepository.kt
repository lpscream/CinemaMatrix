package net.matrixhome.kino.model

import java.io.Serializable

data class GenresRepository(val results: ArrayList<Genre>): Serializable

data class Genre(val id: String, val title: String, val category: String): Serializable
