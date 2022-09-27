package io.fluffistar.ANiFLi.Backend

import android.util.Log

import io.fluffistar.ANiFLi.Serializer.*
import io.fluffistar.ANiFLi.pmap
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TMDB {
    companion object{
        val ImgPath = "https://image.tmdb.org/t/p/original/"


        suspend  fun getTV(name : Serie ) : TvShow2 = suspendCoroutine {

                cont-> setup(name)   { cont.resume(it) }
        }




        fun setup(name: Serie, callback: (TvShow2) -> Unit)  = runBlocking{

            var src: String = if (name.name.length <= 34) name.name else name.name.substring(
                0,
                34
            )
            val pagesrc =  Verwaltung.getJson("https://api.themoviedb.org/3/search/tv?api_key=${APIKEY.tmdb}&query=${src}&language=de")
            var output :TvShow2? = null
            val page : Page  = Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString(pagesrc)
            if (page.total_results > 1){
                for ( tv in page.results)
                {
                    var  x : Int;
                    try
                    {
                        // ;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            x = LocalDate.parse(tv.first_air_date).year
                        }else
                            x =  (tv.first_air_date.split('-')[0]).toInt()
                    }
                    catch( ex : Exception)
                    {
                        x = 0;
                    }

                    if (x == name.productionStart  )
                    {
                        val url = "https://api.themoviedb.org/3/tv/${tv.id}?api_key=${APIKEY.tmdb}&language=de"
                        Log.d("URL",url)
                        val ss = Verwaltung.getJson(url)
                        output =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(ss);
                    }

                }
                if(output == null ) {
                    val url = "https://api.themoviedb.org/3/tv/${page.results[0].id}?api_key=${APIKEY.tmdb}&language=de"
                    Log.d("URL",url)

                    val ss =
                        Verwaltung.getJson(url)
                    output = Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(ss);
                }
            }
            else{
                val url = "https://api.themoviedb.org/3/tv/${page.results[0].id}?api_key=${APIKEY.tmdb}&language=de"
                Log.d("URL",url)
                var sss = Verwaltung.getJson(url)
                output =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(sss);


        }

            callback(output)
        }


        suspend fun work(input:  Seaso , id : Long) {
    val url = "https://api.themoviedb.org/3/tv/${id}/season/${input.seasonNumber}?api_key=${APIKEY.tmdb}&language=de"
            Log.d("URL",url)
            val json = Verwaltung.getJson(url);

            input.episodes = Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<Season2>(json).episodes;
        }
        suspend  fun getTV2(name : String ) : TvShow2 = suspendCoroutine {

                cont-> setup2(name)   { cont.resume(it) }
        }

        fun setup2(name: String, callback: (TvShow2) -> Unit)  = runBlocking {


            val pagesrc =
                Verwaltung.getJson("https://api.themoviedb.org/3/tv/${name}?api_key=${APIKEY.tmdb}&language=de")
            var output: TvShow2? = null
            output = Json {
                ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true
            }.decodeFromString(pagesrc)
            callback(output!!)
        }


        suspend  fun getTVimdb(name : String ) : FindResult = suspendCoroutine {

            cont-> setupimdb(name)   { cont.resume(it) }
        }

        fun setupimdb(name: String, callback: (FindResult) -> Unit)  = runBlocking {
            val url = "https://api.themoviedb.org/3/find/${name}?api_key=${APIKEY.tmdb}&external_source=imdb_id&language=de"
            Log.d("url= ", url)

            val pagesrc =
                    Verwaltung.getJson(url)
            var output: FindResult? = null
            output = Json {
                ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true
            }.decodeFromString(pagesrc)

            callback(output!!)
        }

       suspend fun GetTvEpisodes(tv :TvShow2) {
            tv.seasons.pmap {
                GlobalScope.launch {
                    work(it,tv.id)
                }

            }
        }

           fun newwork(input:  String , id : Int, callback: (List<Episode2>) -> Unit) = runBlocking{
            val url = "https://api.themoviedb.org/3/tv/${id}/season/${input}?api_key=${APIKEY.tmdb}&language=de"
            Log.d("URL",url)
            val json = Verwaltung.getJson(url);
               var output : List<Episode2> = mutableListOf()
            try {
              output =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<Season2>(json).episodes
            }catch (ex : Exception ){ }
          callback(output);
        }

        suspend fun  GetTvEpisode(id : Int , Season : String) : List<Episode2> = suspendCoroutine {
                cont-> newwork(Season,id)   { cont.resume(it) }
        }


        fun setupbetter(name: String, productionStart: Int, callback: (TvShow2) -> Unit)  = runBlocking{

            var src: String = if ( name.length <= 34)  name else  name.substring(
                0,
                34
            )
            val pagesrc =  Verwaltung.getJson("https://api.themoviedb.org/3/search/tv?api_key=${APIKEY.tmdb}&query=${src}&language=de")
            var output :TvShow2? = null
            val page : Page  = Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString(pagesrc)
            if (page.total_results > 1){
                for ( tv in page.results)
                {
                    var  x : Int;
                    try
                    {
                        // ;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            x = LocalDate.parse(tv.first_air_date).year
                        }else
                            x =  (tv.first_air_date.split('-')[0]).toInt()
                    }
                    catch( ex : Exception)
                    {
                        x = 0;
                    }

                    if (x ==  productionStart  )
                    {
                        val url = "https://api.themoviedb.org/3/tv/${tv.id}?api_key=${APIKEY.tmdb}&language=de"
                        Log.d("URL",url)
                        val ss = Verwaltung.getJson(url)
                        output =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(ss);
                    }

                }
                if(output == null ) {
                    val url = "https://api.themoviedb.org/3/tv/${page.results[0].id}?api_key=${APIKEY.tmdb}&language=de"
                    Log.d("URL",url)

                    val ss =
                        Verwaltung.getJson(url)
                    output = Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(ss);
                }
            }
            else{
                val url = "https://api.themoviedb.org/3/tv/${page.results[0].id}?api_key=${APIKEY.tmdb}&language=de"
                Log.d("URL",url)
                var sss = Verwaltung.getJson(url)
                output =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<TvShow2>(sss);


            }

            callback(output)
        }

        suspend fun GetTVShow(Title : String ,productionStart : Int ) : TvShow2 = suspendCoroutine {

                cont-> setupbetter(Title,productionStart)   { cont.resume(it) }
        }

    }
}