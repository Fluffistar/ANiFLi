package io.fluffistar.ANiFLi.Serializer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
public class TVShowMin
(
     val original_name :String ,
val  name  : String,
val   popularity  : Double,
val   origin_country : List<String>,
val  vote_count :Int,
val  first_air_date :String,
val  backdrop_path :String = "",
val  original_language :String ,
val id : Int,
val  vote_average : Double,
val  overview  : String,
val poster_path : String = ""
)
@Serializable
class Page(val page :Int , val total_results :Int ,val total_pages :Int , val results: List<TVShowMin>)
{

}
@Serializable
class TVShow
    (
    val  backdrop_path : String = "",
    val   episode_run_time : List<Int>,
    val   first_air_date :String ,
    val   homepage :String,
    val   id :Int,
    val   in_production :Boolean,
    val   languages  : List<String>,
    val   last_air_date :String,
    val   name :String,
    val  next_episode_to_air :TvEpisode,
    val  number_of_seasons :Int ,
    val   overview :String,
    val   popularity :Double,
    val   poster_path :String = "",
    val  seasons :List<TvSeason>,
    val   status :String,
    val   vote_average :Double
)
@Serializable
 class TvSeason
    (
    val   air_date :String,
    val  episode_count :Int,
    val  id :Int,
    val  name :String,
    val  overview :String,
    val  poster_path :String = "",
    val  season_number : Int,
    var  episodes  :List<TvEpisode>,
    var TvID : Int

)
@Serializable
 class TvEpisode
(
    val   overview :String ,
    val   air_date :String,
    val   episode_number :Int,
    val   id :Int,
    val   name :String,
    val   season_number :Int,
    val   stillpath  :String
)


@Serializable
data class TvShow2 (
    @SerialName( "backdrop_path")
    val backdropPath: String = "",



     @SerialName(  "episode_run_time")
    val episodeRunTime: List<Long>  ,

    @SerialName(  "first_air_date")
    val firstAirDate: String,

    val genres: List<Genre>,
    val homepage: String,
    val id: Long,

    @SerialName(  "in_production")
    val inProduction: Boolean,

    val languages: List<String>,

    @SerialName(  "last_air_date")
    val lastAirDate: String,

    @SerialName(  "last_episode_to_air")
    val lastEpisodeToAir: LastEpisodeToAir,

    val name: String,

    @SerialName(  "next_episode_to_air")
    val nextEpisodeToAir: Episode2? = null ,

    val networks: List<Network>,

    @SerialName(  "number_of_episodes")
    val numberOfEpisodes: Long,

    @SerialName(  "number_of_seasons")
    val numberOfSeasons: Long,

    @SerialName(  "origin_country")
    val originCountry: List<String>,

    @SerialName(  "original_language")
    val originalLanguage: String,

    @SerialName(  "original_name")
    val originalName: String,

    val overview: String,
    val popularity: Double,

    @SerialName( "poster_path")
    val posterPath: String  = "",



    @SerialName( "production_countries")
    val productionCountries: List<ProductionCountry>,

    val seasons: List<Seaso>,

    @SerialName(  "spoken_languages")
    val spokenLanguages: List<SpokenLanguage>,

    val status: String,
    val tagline: String,
    val type: String,

    @SerialName(  "vote_average")
    val voteAverage: Double,

    @SerialName(  "vote_count")
    val voteCount: Long
)
@Serializable
data class Genre (
    val id: Long,
    val name: String

)


class Genres(var name : String = "",var series : List<Serie> = mutableListOf()){


}

@Serializable
data class LastEpisodeToAir (
    @SerialName( "air_date")
    val airDate: String = "",

    @SerialName(  "episode_number")
    val episodeNumber: Long,

    val id: Long,
    val name: String,
    val overview: String,

    @SerialName(  "production_code")
    val productionCode: String,

    @SerialName(  "season_number")
    val seasonNumber: Long,

    @SerialName(  "still_path")
    val stillPath: String = "",

    @SerialName( "vote_average")
    val voteAverage: Double,

    @SerialName(  "vote_count")
    val voteCount: Long
)
@Serializable
data class Network (
    val name: String = "",
    val id: Long = 0,

    @SerialName(   "logo_path")
    val logoPath: String = "",

    @SerialName( "origin_country")
    val originCountry: String = ""
)
@Serializable
data class ProductionCountry (
    @SerialName(  "iso_3166_1")
    val iso3166_1: String,

    val name: String
)
@Serializable
data class Seaso (
    @SerialName(  "air_date")
    val airDate: String = "",

    @SerialName( "episode_count")
    val episodeCount: Long,

    val id: Long,
    val name: String,
    val overview: String,
    var  episodes  :List<Episode2> = mutableListOf(),
    @SerialName(  "poster_path")
    val posterPath: String = "",

    @SerialName(  "season_number")
    val seasonNumber: Long
)
@Serializable
data class SpokenLanguage (
    @SerialName(  "english_name")
    val englishName: String,

    @SerialName(  "iso_639_1")
    val iso639_1: String,

    val name: String
)
@Serializable
data class Season2 (
    @SerialName( "_id")
    val id: String,

    @SerialName(  "air_date")
    val airDate: String = "",

    var episodes: List<Episode2>,
    val name: String,
    val overview: String,

    @SerialName( "id")
    val welcome10ID: Long,

    @SerialName( "poster_path")
    val posterPath: String = "",

    @SerialName( "season_number")
    val seasonNumber: Long
)
@Serializable
data class Episode2 (
    @SerialName(  "air_date")
    val airDate: String = "",

    @SerialName( "episode_number")
    val episodeNumber: Long,


    @SerialName( "guest_stars")
    val guestStars: List<GuestStar> = mutableListOf(),

    val id: Long,
    val name: String,
    val overview: String,

    @SerialName( "production_code")
    val productionCode: String,

    @SerialName( "season_number")
    val seasonNumber: Long,

    @SerialName( "still_path")
    val stillPath: String = "",

    @SerialName( "vote_average")
    val voteAverage: Double,

    @SerialName( "vote_count")
    val voteCount: Long
)
@Serializable
data class GuestStar (
    val character: String,

    @SerialName( "credit_id")
    val creditID: String,

    val order: Long = 0,
    val adult: Boolean = false ,
    val gender: Long = 0,
    val id: Long = 0,

    @SerialName( "known_for_department")
    val knownForDepartment: KnownForDepartment? = null,

    val name: String = "",

    @SerialName( "original_name")
    val originalName: String = "",

    val popularity: Double = 0.0,

    @SerialName( "profile_path")
    val profilePath: String? = null
)

enum class KnownForDepartment(val value: String) {
    Acting("Acting");

    companion object {
        public fun fromValue(value: String): KnownForDepartment = when (value) {
            "Acting" -> Acting
            else     -> throw IllegalArgumentException()
        }
    }
}