package io.fluffistar.ANiFLi.Serializer

import kotlinx.serialization.Serializable

@Serializable
class Settings(var autplay: Boolean ,var synwatchlist : Boolean , var showsub : Boolean) {
}