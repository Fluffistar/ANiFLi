package io.fluffistar.ANiFLi.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = TVResult::class,
                    parentColumns = ["id"],
                    childColumns = ["TVResultid"],
                    onDelete = ForeignKey.CASCADE, // or any other strategy
                    onUpdate = ForeignKey.CASCADE,) // or any other strategy
                    // deferred = true
            ,        ForeignKey(
                    entity = Genre::class,
                    parentColumns = ["id"],
                    childColumns = ["genre"],
                    onDelete = ForeignKey.CASCADE, // or any other strategy
                    onUpdate = ForeignKey.CASCADE, // or any other strategy
                    // deferred = true
            )    ,        ForeignKey(
                    entity = TVShow::class,
                    parentColumns = ["id"],
                    childColumns = ["TVShowid"],
                    onDelete = ForeignKey.CASCADE, // or any other strategy
                    onUpdate = ForeignKey.CASCADE, // or any other strategy
                    // deferred = true
            )
        ]
)

data class Serie(@PrimaryKey private val id: Int,
                 private val title:String,
                 private val thumbnail:String = "",
                 private val imdb : String = "" ,
                 private val background: String = "",
                 private val genre: String  ,
                 @ColumnInfo(index = true) private val TVResultid: Int?,
                 @ColumnInfo(index = true) private val TVShowid : Int?,
                 private val productionStart :Int  =0
                 ) {
}

@Entity(
        foreignKeys = [
            ForeignKey(
                    entity = Serie::class,
                    parentColumns = ["id"],
                    childColumns = ["serie"],
                    onDelete = ForeignKey.CASCADE, // or any other strategy
                    onUpdate = ForeignKey.CASCADE,) // or any other strategy
            // deferred = true

        ]
)

data class Season(@PrimaryKey private val id: Int, @ColumnInfo(index = true) private val serie : Int,private val Season : Int, private val Special : Boolean , private val url_Season : String)



@Entity
data class Genre(@PrimaryKey private val id: Int,private val name :String)

@Entity
data class TVResult(@PrimaryKey private val id : Int ,private val poster_path : String = "", private val popularity : Double,private val tvid : Int,private val backdrop_path :String, private val vote_average :Double,private val overview :String, private val first_air_date :String, private val name :String )

@Entity
data class TVShow(@PrimaryKey private val id : Int , private val backdropPath: String, private val firstAirDate: String , private val tvid: Int,private val inProduction: Boolean,private val nextEpisodeToAirDAte : String ,private val posterPath: String)