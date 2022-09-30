package io.fluffistar.ANiFLi.Backend

import android.content.Context
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import io.fluffistar.ANiFLi.Serializer.*
import io.fluffistar.ANiFLi.pmap
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class HosterName(val id: Int, val name: String)
class Language(val id: Int, val lang: String)

class Verwaltung {
    companion object{

        fun getBetween(html: String, start: String, end: String) : String{

            val regex =  """(?<=$start)(.*)(?=$end)""".toRegex()
            val cryptlink = regex.find(html)?.value.orEmpty()
            return  cryptlink
        }

        fun getBetween2(html: String, start: String, end: String):String{
            val Start: Int
            val End: Int
            return if (html.contains(start) && html.contains(end)) {
                Start = html.indexOf(start, 0) + start.length
                End = html.indexOf(end, Start)
                html.substring(Start, End  )
            } else {
                ""
            }
        }


        val main ="https://serien.pro"
        var SelectedSerie : Serie? = null
        var Session : String = ""
        var Settings : Settings = Settings(false, false, false)
        val allSerie: String = "api/v1/series/list?extended=1&category=0"
        var Neu: String = "api/v1/series/list?extended=1&category=2"
        var Top: String = "api/v1/series/list?extended=1&category=3"
        var Beliebt: String = "api/v1/series/list?extended=1&category=1"
        var SerieGet: String = "api/v1/series/get?series="
        var New_Episodes: String = "api/v1/episodes/updates"
        val AllGenres : MutableList<Genres> = mutableListOf()
        val HosterNames : MutableList<HosterName> = mutableListOf()
        val Languages : MutableList<Language> = mutableListOf()
        var _hosternames = arrayOf<String>("Vivo", "VOE", "Vidoza", "Streamtape")
        var linkname = arrayOf<String>(
            "Abenteuer",
            "Action",
            "Animation",
            "Anime",
            "Comedy",
            "Dokumentation",
            "Dokusoap",
            "Drama",
            "Dramedy",
            "Familie",
            "Fantasy",
            "History",
            "Horror",
            "Jugend",
            "Kinderserie",
            "Krankenhaus",
            "Krimi",
            "Mystery",
            "Romantik",
            "Science-Fiction",
            "Sitcom",
            "Telenovela",
            "Thriller",
            "Western",
            "Zeichentrick",
            "K-Drama",
            "Reality-Tv",
            "Netflix-Originals",
            "Amazon-Originals"
        )

        var laoded = false
        var _AllSeries : MutableList<Serie> =  (mutableListOf())
        var _NeuSeries : MutableList<Serie> =  (mutableListOf())
        var _BeliebtSeries : MutableList<Serie> =  (mutableListOf())
  //      var _TopSeries : Series = Series(mutableListOf())
        var _WatchSeries : Series = Series(mutableListOf())
  //      var _Sublist :Series = Series(mutableListOf())
        private var gen = false
        val otherpattern = """(?<=<h3>)(.*)(?=<span)""".toRegex()
        val urlall  =  URL("https://serien.pro/serien")
        val urlbeliebt = URL("https://serien.pro/beliebte-serien")
        val urlnewu = URL("https://serien.pro/neu")
        fun Setup(context: Context) = runBlocking {

            val sharedPref = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            Session = sharedPref.getString("SessionID", "").orEmpty()
            getWatchedSeries(context)
              val conn: HttpURLConnection =
            urlall.openConnection() as HttpURLConnection // create a connection
            var input: String = conn.inputStream.bufferedReader().use(BufferedReader::readText)
            val allpatern = """<a data-alternative-title=(.*?) href(.*?)</a>""".toRegex()
            input = input.replace("\n", "").split("id=\"seriesContainer\"")[1]
            val genres = input.split("</ul>")
            val genpmap =   genres.pmap {
                val all = allpatern.findAll(it)

          /*      all.pmap { it2 ->

                    val s = Serie(it2, it)
                    _AllSeries.add(s)

                }*/

            }

            val conn2: HttpURLConnection =
                urlbeliebt.openConnection() as HttpURLConnection // create a connection
            var input2: String = conn2.inputStream.bufferedReader().use(BufferedReader::readText)
            val beliebtdata =  otherpattern.findAll(input2)
     /*       beliebtdata.asIterable().pmap { match->
                val s=  _AllSeries.find { it.name == match.groupValues[1] }
                if (s != null)
                    _BeliebtSeries.add(s)
            }*/
            val conn3: HttpURLConnection =
                urlnewu.openConnection() as HttpURLConnection // create a connection
            var input3: String = conn3.inputStream.bufferedReader().use(BufferedReader::readText)
            val newseriedata =  otherpattern.findAll(input3)
         /*   var new = newseriedata.asIterable().pmap {

                    match->
                val s=  _AllSeries.find { it.name == match.groups[1]?.value }
                if (s != null)
                    _NeuSeries.add(s)
            }*/

                laoded = true

        }

   suspend fun GetSerie(id: String): SelectedSerie {
            return Json{ignoreUnknownKeys = true}.decodeFromString<SelectedSerie>(
                getJson(
                    getKey(
                        main + SerieGet + id
                    )
                )
            );
        }

        fun saveSettings(context: Context){
            val string =   Json.encodeToString(Settings)
            createfile("setting.json", string, context)
        }

        fun laodSettings(context: Context){
            val string  = readFromFile(context, "setting.json")
            if(!string.isNullOrBlank())
              Settings =  Json { ignoreUnknownKeys = true }.decodeFromString(string)

        }

       suspend  fun getJson(url: String) : String = suspendCoroutine {

               cont-> geturl(url)   { cont.resume(it) }
             }

         fun geturl(url: String, callback: (String) -> Unit){

            Thread {
                Log.d("allSerie", "geturl")
                val (_, _, result) = Fuel.get(url)
                    .responseString()
                Log.d("allSerie", "geturl")
                when (result) {
                    is Result.Failure -> {
                        Log.d("allSerie", result.getException().toString())
                        callback("")

                    }
                    is Result.Success -> {
                        val data = result.get()
                        Log.d("allSerie", data)
                        //
                        callback(data)

                    }
                }
            }.start()
    }






        suspend fun  processfinsihed() =  withContext(Dispatchers.IO){
            launch { laoded = true }
        }


        suspend  fun getWatch(url: String) : String = suspendCoroutine {

                cont-> geturlWatch(url)   { cont.resume(it) }
        }
        fun geturlWatch(url: String, callback: (String) -> Unit){

            Thread {
                Log.d("allSerie", "geturl")
                val (_, _, result) = Fuel.post(url).header(
                    "Cookie",
                    "SSTOSESSION=${Verwaltung.Session}"
                )
                    .responseString()
                Log.d("allSerie", "geturl")
                when (result) {
                    is Result.Failure -> {
                        Log.d("allSerie", result.getException().toString())
                        callback("")

                    }
                    is Result.Success -> {
                        val data = result.get()
                        Log.d("allSerie", data)
                        //
                        callback(data)

                    }
                }
            }.start()
        }


        fun readFromFile(context: Context, file: String): String? {

            var ret = ""
            try {
                val inputStream: InputStream? = context.openFileInput(file)
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { receiveString = it } != null) {
                        stringBuilder.append("\n").append(receiveString)
                    }
                    inputStream.close()
                    ret = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                Log.e("login activity", "File not found: " + e.toString())
            } catch (e: IOException) {
                Log.e("login activity", "Can not read file: $e")
            }
            return ret
        }


        fun addwatch(s: Serie, context: Context){

            if(_WatchSeries.series.find { it.name == s.name } == null)
                _WatchSeries.series.add(s)
            else {
                _WatchSeries.series.removeAll { it.name == s.name }
                _WatchSeries.series.add(s)
            }
            val string =   Json.encodeToString(_WatchSeries)
            Log.d("watchjson",string.toString())
            createfile("watch.json", string, context)


        }

        fun getWatchedSeries(context: Context)  {


           val string  = readFromFile(context, "watch.json")
            Log.d("watchjson",string.toString())
              if(!string.isNullOrBlank()){
               val json: Series = Json { ignoreUnknownKeys = true }.decodeFromString(string)
                  for(s in json.series) {
                      Log.d("SeriesWAtched", s.name)
                      if (_WatchSeries.series.find { it.name == s.name } == null)
                          _WatchSeries.series.add(s)
                  }
            }else
                _WatchSeries = Series(mutableListOf())
        }

        fun createfile(file: String, data: String, context: Context){

            try {
                val outputStreamWriter = OutputStreamWriter(
                    context.openFileOutput(
                        file,
                        Context.MODE_PRIVATE
                    )
                )
                outputStreamWriter.write(data)
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: " + e.toString())
            }
        }



     fun  getKey(s: String) : String =if (s.contains("?")) s+ "&key=${APIKEY.key}" else  s + "?key=${APIKEY.key}"

        fun getUpdate(context: Context) : Pair<Boolean,String>   {

             val  result = Fuel.get("https://api.github.com/repos/Fluffistar/ANiFLi/releases").header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36").responseString ()

            when(result.third){
                is Result.Failure -> {
                    return Pair(false,"")

                }
                is Result.Success -> {

                   val release = Json { ignoreUnknownKeys = true ; coerceInputValues = true }.decodeFromString<List<Release2Item>>(result.third.get())[0]


                    if(release.tag_name.replace(".","").toInt() > context.packageManager.getPackageInfo(context.packageName, 0).versionName .replace(".","").toInt()){

                        return Pair(true,release.assets[1].browser_download_url!!)
                    }


                }
            }
                 return Pair(false,"")


    }





    }
}