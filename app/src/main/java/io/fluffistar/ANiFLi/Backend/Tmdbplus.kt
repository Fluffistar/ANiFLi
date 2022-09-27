package io.fluffistar.ANiFLi.Backend

import kotlinx.serialization.Serializable

class Tmdbplus {
companion object{
  val  imgpath  : String=  "https://image.tmdb.org/t/p/original/"
}


}

@Serializable
 class MovieResult
     (
    var   poster_path :String = "",
    var   adult :Boolean,
    var   overview :String = "",
    var   release_date :String = "",
    var genre_ids : List<Int> ,

    var   id :Int,
    var   first_air_date :String = "",
    var   original_title :String = "",
    var   original_language :String = "",
    var   title :String = "",
    var   backdrop_path :String = "",

    var   popularity :Double,
    var   vote_count :Int,
    var   video :Boolean,
    var   vote_average :Double,

    )
@Serializable
 class FindResult
(
    var  movie_results :List<MovieResult>,

    var  tv_results :List<TVResult>
)
@Serializable
 class TVResult
(

    var   poster_path :String = "",
    var   popularity :Double,
    var   id :Int,
    var   backdrop_path :String = "",
    var   vote_average :Double,

    var   overview :String = "",
    var   first_air_date :String = "",
    var  origin_country :List<String>,
    var  genre_ids :List<Int>,
    var   original_language :String = "",
    var   vote_count :Int,

    var   name :String = "",
    var   original_name :String

)


@Serializable
 class Genre
(
    var   id :Int,
    var     name :String = "",
)
@Serializable
 class LastEpisodeToAir
(
    var   air_date :String = "",
    var   episode_number :Int,
    var   id :Int,
    var   name :String = "",
    var   overview :String = "",
    var   production_code :String = "",
    var   season_number :Int,
    var   still_path :String = "",
    var   vote_average :Double,
    var   vote_count :Int,
)
@Serializable
 class NextEpisodeToAir
(
    var   air_date :String = "",
    var   episode_number :Int,
    var   id :Int,
    var   name :String = "",
    var   overview :String = "",
    var   production_code :String = "",
    var   season_number :Int,
    var   still_path :String = "",
    var   vote_average :Double,
    var   vote_count :Int,
)
@Serializable
 class Network
(
    var   name :String = "",
    var   id :Int,
    var   logo_path :String = "",
    var   origin_country :String = "",
)
@Serializable
 class ProductionCompany
(
    var   id :Int,
    var   logo_path :String = "",
    var   name :String = "",
    var   origin_country :String = "",
)
@Serializable
 class ProductionCountry
(
    var    iso_3166_1 :String = "",
    var   name :String = "",
)
@Serializable
 class Season
(
    var    air_date :String = "",
    var   episode_count :Int,
    var   id :Int,
    var   name :String = "",
    var   overview :String = "",
    var   poster_path :String = "",
    var   season_number :Int,
    var   episodes :List<SpokenLanguage>,

)
@Serializable
 class TvEpisode
(
    var   overview :String = "",
    var   air_date :String = "",
    var   episode_number :Int,
    var     id :Int,
    var   name :String = "",
    var   season_number :Int,
    var   still_path :String = "",
)
@Serializable
 class SpokenLanguage
(
    var   english_name :String = "",
    var   iso_639_1 :String = "",
    var   name :String = "",
)
@Serializable
 class TV
(
    var   backdrop_path :String = "",
    var created_by :List<String> ,
    var  episode_run_time :List<Int>,
    var   first_air_date :String = "",
    var  genres :List<Genre>,
    var   homepage :String = "",
    var   id :Int,
    var   in_production :Boolean,
    var  languages :List<String>,
    var   last_air_date :String = "",
    var  last_episode_to_air :LastEpisodeToAir,
    var   name :String = "",
    var  next_episode_to_air :NextEpisodeToAir,
    var  networks :List<Network>,
    var   number_of_episodes :Int,
    var   number_of_seasons :Int,
    var  origin_country :List<String>,
    var   original_language :String = "",
    var   original_name :String = "",
    var   overview :String = "",
    var   popularity :Double,
    var   poster_path :String = "",
    var  production_companies :List<ProductionCompany>,
    var  production_countries :List<ProductionCountry>,
    var  seasons :List<Season>,
    var  spoken_languages :List<SpokenLanguage>,
    var   status :String = "",
    var   tagline :String = "",
    var   type :String = "",
    var   vote_average :Double,
    var   vote_count :Int,
)

