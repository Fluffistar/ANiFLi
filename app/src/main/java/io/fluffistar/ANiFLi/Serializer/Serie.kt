package io.fluffistar.ANiFLi.Serializer

import android.util.Log
import io.fluffistar.ANiFLi.Backend.*
import io.fluffistar.ANiFLi.Backend.Verwaltung.Companion.SerieGet
import io.fluffistar.ANiFLi.Backend.Verwaltung.Companion.getJson
import io.fluffistar.ANiFLi.Backend.Verwaltung.Companion.getKey
import io.fluffistar.ANiFLi.Backend.Verwaltung.Companion.main
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@Serializable
class Serie(): java.io.Serializable
{
    var name: String =""
    var link: String =""
    var cover: String = "https://www.cmt.co.uk/skin/frontend/cmtgroup/default/images/placeholder2.jpg"
    var background: String = ""
    var description: String = ""
    var imdb : String = ""
    var genre: String = ""
    var TVResult :TVResult? = null
    var TVShow : TvShow2? = null
    var productionStart :Int  = 0
    var lastepisode : Int = 0
    var lastseason : Int = 0
    var SeasonsList : MutableList<SeasonSTO> = mutableListOf()
    @Serializable
    class SeasonSTO (){

          var name = "";

        var Season :Int = -1
        var special = false ;
        var loadedSeason = false
        var url_season =""
        val  Episodes : MutableList<EpisodeSTO> = mutableListOf()


        constructor(match: MatchResult, _url: String):this() {
            url_season = _url;
            var title = Verwaltung.getBetween(match.value, "title=\"", "\">");

            name = if(title != "Alle Filme")  title.replace("Staffel", "Season") else  "Specials";

            if (name != "Specials")
            {
                Season =  (Verwaltung.getBetween(match.value, "\">", "</a>").toInt());
            }
            else
            {
                Season = 0;
                special = true;
            }

            GlobalScope.launch(Dispatchers.IO) {
                load()
            }

        }

        override fun toString(): String {
            return  this.name
        }

       suspend fun  load(){
                var result = ""
                result = Verwaltung.getJson(main + url_season + if (Season == 0) "/filme" else "/staffel-${Season}");
           Log.d("Episodeurl" , main + url_season + if (Season == 0) "/filme" else "/staffel-${Season}")
                result = Verwaltung.getBetween2(
                    result,
                    "<tbody",
                    "</table>"
                ) ;

           Log.d("Episoderesult" , result)
                val season_pattern = """<a href(.*?)</a>""".toRegex()

                for (  match in season_pattern.findAll(result))
                {
                    var data = Verwaltung.getBetween2(
                        match.value,
                        if (Season == 0) "film-" else "episode-",
                        "\"> <"
                    );
                    if(data != "")
                        if ( Episodes.filter {     it.Episode ==data.toInt()}.isNullOrEmpty()  )
                    {
                        Log.d("Episode",EpisodeSTO(match, Season).toString())
                        Episodes.add(EpisodeSTO(match, Season));
                    }
                }
                loadedSeason = true
            }

    }
    @Serializable
    class EpisodeSTO(){
        var Episode = -1
        var Season = -1
        var german: String = ""
        var english: String = ""
        var German: MutableList<Hoster> = mutableListOf()
        var English: MutableList<Hoster> = mutableListOf()
        var Sub: MutableList<Hoster> = mutableListOf()
        var url: String = ""
            constructor(match: MatchResult, season: Int):this (){
                Season = season;
                if(Season !=0)
                    Episode =  (Verwaltung.getBetween2(match.value, "episode-", "\">").toInt());
                else
                    Episode =  (Verwaltung.getBetween2(match.value, "film-", "\">")).toInt()
                url = Verwaltung.getBetween2(match.value, "href=\"", "\">");
                german = Verwaltung.getBetween2(match.value, "<strong>", "</strong>");
                english = Verwaltung.getBetween2(match.value, "<span>", "</span>");
            }

        override fun toString(): String {
            return  "Episode $German $English"
        }
       suspend fun load(){
            var result = Verwaltung.getJson(main + url);
            val hoster_pattern = """<li class="col-md-4 col-xs-12(.*?)</li>""".toRegex()
            result = Verwaltung.getBetween2( result, "originalLinkTarget", "</ul");
               Log.d("Logresult",result)
            var rem = """"\t|\n|\r|\s"""".toRegex()
           result =  result.replace(rem,"")

           Log.d("Logrem",result)
            for (  match in hoster_pattern.findAll (result ))
            {
                Log.d("Logmatch",match.value)
                if(Verwaltung._hosternames.contains( (Verwaltung.getBetween2(match.value, "<h4>", "</h4>"))))
                    when(

                        Integer.parseInt( (Verwaltung.getBetween(match.value, "data-lang-key=\"", "\" data-link-id").replace(
                                "\" ",
                                ""
                            ) ))

                    )
                    {
                          1 -> {
                              German.add(  Hoster (match));
                          }
                          2-> {
                              English.add(  Hoster (match));
                          }
                        3 -> {
                            Sub.add(  Hoster (match));
                        }
                    }

            }

        }
        @Serializable
        class Hoster(){
            var Hostername: String = ""
            var Language = 0
            var link: String = ""
            constructor(match: MatchResult ):this (){
                Hostername = Verwaltung.getBetween(match.value,"<h4>","</h4>");
                var lang_string = (Verwaltung.getBetween(match.value, "data-lang-key=\"", "\" data-link-id").replace(
                    "\" ",
                    ""
                ) )
                Language =  (lang_string).toInt();
                link =  main+ Verwaltung.getBetween(match.value , "href=\"","\" target").replace("\"", "");
            }
        }
    }

    constructor(
        name: String,
        link: String,
        cover: String,
        background: String,
        description: String,
        genre: String,
        productionStart: Int
    ) : this(){

    }
    constructor(match: MatchResult, _genre: String) :this() {
        name = Verwaltung.getBetween(match.value, "anschauen\">", "</a>")
        link = Verwaltung.getBetween(match.value, "href=\"", "\" title")
        genre= Verwaltung.getBetween(_genre, "<h3>", "</h3>")
    }
   suspend fun getImdbVal():String{
        result= getJson(main + link)
       val imdb_pattern = """href[^>]*>(.*?)IMDB</a>""".toRegex()
        var match = imdb_pattern.find(result);
       var imdb_id = ""
        if(match != null)
         imdb_id = Verwaltung.getBetween(match?.value!!, "https://www.imdb.com/title/", "\" title");



        return imdb_id;
    }

    fun getproductionstart() : Int{
        return  0

    }
    fun   getTV() :Boolean
    {


      /*  if(TVResult != null)
            TV = Json.decodeFromString<TV>(new WebClient().DownloadString($"https://api.themoviedb.org/3/tv/{TVResult.id}?api_key={APIKEY.tmdb}&language=de"));
        else if(TVShow != null)
        TV =  JsonConvert.DeserializeObject<TV>(new WebClient().DownloadString($"https://api.themoviedb.org/3/tv/{TVShow.id}?api_key={APIKEY.tmdb}&language=de"));


        return TV != null ? true : false;*/
        return  false

    }
   fun vResult(_imdb: String, callback: (TVResult?) -> Unit) = runBlocking  {
        val findResult =  Json{ignoreUnknownKeys = true;coerceInputValues = true;isLenient = true}.decodeFromString<FindResult>(
            Verwaltung.getJson(
                "https://api.themoviedb.org/3/find/${imdb}?api_key=${APIKEY.tmdb}&external_source=imdb_id&language=de"
            )
        );

       var output : TVResult? =null
       if (findResult.tv_results.size > 0)
       {
           cover = TMDB.ImgPath + findResult.tv_results[0].poster_path;
           output =(  findResult.tv_results[0]);
       }

           callback(output)
   }
    suspend  fun getvResult(_imdb: String) : TVResult? = suspendCoroutine {

            cont->  vResult(_imdb)   { cont.resume(it) }
    }
    private var result = ""

    suspend  fun load(): Boolean{
        if ( cover == "https://www.cmt.co.uk/skin/frontend/cmtgroup/default/images/placeholder2.jpg") {
            imdb = getImdbVal()
            productionStart = getproductionstart()

            if (imdb != "")
                TVResult =  getvResult(imdb);
            else {
                try
                {
                    TVShow =   null; //gettvshow
                    cover = TMDB.ImgPath  //TVShow?.posterPath;
                }catch (ex: Exception) {

                    cover = main + Verwaltung.getBetween(
                        result.replace("\n", ""),
                        "<noscript><img src=\"",
                        "\" alt=\""
                    );
                    if(cover == "")
                        Log.d("result", result);
                }
            }
            if (cover == "https://image.tmdb.org/t/p/original/"|| cover == "https://www.cmt.co.uk/skin/frontend/cmtgroup/default/images/placeholder2.jpg" || cover == "https://image.tmdb.org/t/p/original/null"  || cover == null)
                cover = main+ Verwaltung.getBetween(
                    result.replace("\n", ""),
                    "<noscript><img src=\"",
                    "\" alt=\""
                );
            if (cover == "")
                Log.d("result", result);
            return true
    }
        else
        return true
    }

     fun loadSeasons(){
        Log.d("LoadSeason","Started")
       val season_pattern = """<a(.*?)href(.*?)</a>""".toRegex();

       result.replace("""\t|\n|\r""".toRegex(),"")

       val season_data = Verwaltung.getBetween2(result , "id=\"stream\"", "class=\"cf\"")
         Log.d("LoadSeason",season_data   )

         var last : SeasonSTO? = null;

       for (  match in season_pattern.findAll(season_data ))
       {
           var season :SeasonSTO =   SeasonSTO(match, link);
           Log.d("LoadSeason",season.name)
           if ( SeasonsList.filter{ it.Season == season.Season}.isNullOrEmpty()) {
           if (Verwaltung.getBetween(match.value, "title=\"", "\">") != "Alle Filme")
               SeasonsList.add(season);
           else
               last = season;

       }

       }

       if (last != null)
           SeasonsList.add(last);

         Log.d("LoadSeason","Finished")
    }

    override fun toString(): String {
         return  "Title: $name link $link"
    }

}

@Serializable
class Series(var series: MutableList<Serie>){
}

@Serializable
public class SelectedSerie
    (

    var finished: Boolean = false,
    val series: Serie,
    val seasons: List<Int>,
    var SeasonsList: MutableList<Seasons> = mutableListOf(),
    var last: Seasons? = null,
    var hasSpecial: Boolean = false,
    var SelectedSeason: Int = 0,
    var SelectedEpisode: Int = 0
) : java.io.Serializable {
    suspend fun CreateSeasons()
    {
        SeasonsList = mutableListOf()
        if (finished == false)
        {

            for (i in seasons)
            {
                val request : String = getJson(getKey(main + SerieGet + series.name + "&season=" + i));

                val seaso : Seasons = Json{ignoreUnknownKeys = true;coerceInputValues = true}.decodeFromString<Seasons>(
                    request
                );
                seaso.name = if(i == 0)  "Specials" else "Season " + i;
                seaso.id = i;

                if (i != 0)
                    SeasonsList.add(seaso);
                else
                {
                    last = seaso;
                    hasSpecial = true;
                }
            }
            if (last != null)
                SeasonsList.add(last!!);
            finished = true;
        }
    }



}
@Serializable
public class LastTimeEpisodeWatched
    (
    val id: Int,
    val duration: Double,
    val maxduration: Double
)
@Serializable
public class Seasons
    (
    ///if 0 Specials
    //  public bool finished = false;
    var name: String = "",
    var id: Int = -1,
    val episodes: List<Episodes>


): java.io.Serializable {
    override fun toString(): String {
        return name
    }
    }
@Serializable
public class Episodes
    (
    val id: Int,
    val series: Int,
    val season: Int,
    val episode: Int,
    val german: String = "",
    val english: String = "",
    // public string thumbnail { get; set; }
    val description: String = "",
    val links: List<Links>
): java.io.Serializable
@Serializable
public class Links
    (
    val id: Int,
    val link: String,
    val hoster: String,
    val hosterTitle: String,
    val language: Int

): java.io.Serializable{
    override fun toString(): String {
        return  hosterTitle
    }
        }
