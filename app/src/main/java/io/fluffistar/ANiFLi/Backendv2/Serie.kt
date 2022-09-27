package io.fluffistar.ANiFLi.Backendv2

 
import android.util.Log
import io.fluffistar.ANiFLi.Backend.TMDB
import io.fluffistar.ANiFLi.Element
import io.fluffistar.ANiFLi.HtmlParser
import io.fluffistar.ANiFLi.Serializer.Episode2
import io.fluffistar.ANiFLi.Serializer.TvShow2
import io.fluffistar.ANiFLi.View
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.collections.HashSet

@Serializable
class WatchedSerie(val Title : String , val Link : String){

}

public class Serie
{
    var Parser =   HtmlParser();

    var  Lasttime : Date = Date();



    constructor(  title :String,   link : String)
    {
        Title = title;
        this.link = link;
    }


    constructor(  title : String,   link: String,   Lasttime : String)
    {
        Title = title;
        this.link = link;
      //  this.Lasttime = Date.Parse(Lasttime);

    }

    val Title : String


    val link :String


    var  poster : String = ""


    var Elements : List<Element> = mutableListOf();

     var imdb = "";

    //implement find in db if not than load new // after load update db // else async update db
   suspend fun load()
    {
        Elements =   Parser.setup(Start.Domain + link);
        for (  item in Elements)
        {
            if (item.type == View.div)
            {
                if (item.classname == "seriesCoverBox")
                {
                    poster = Start.Domain + item.getChild().src;
                }
                if(item.classname == "backdrop")
                {
                    var data = item.getCustomHeader("style=\"background-image: url(");

                    backdropold =  data.replace(")","");


                }

            }

            if (item.classname == "seri_des")
                olddesription = item.getCustomHeader("data-full-description=\"");
            if (item.classname == "imdb-link")
            {
                imdb = item.getCustomHeader("data-imdb=\"");
            }
            if (item.type == View.span)
            {
                if ("startDate" == item.getCustomHeader("itemprop"))
                {
                    try
                    {

                        productionStart =  (item.getChild().content).toInt();

                    }
                    catch (ex : Exception ) { }
                }
            }
        }


    }



    var productionStart = 0;

    var TvShow : TvShow2? = null;
    var backdrop = "";
    var backdropold = "";

    var BackdropMain  : String = ""

    var despriction = "";

    var olddesription = "";

     var  Seasons : MutableList<Season> = mutableListOf();

    var Tvloaded = false;
    var Seasonlaoded = false;

     fun loadSeasons()
    {
        if (!Seasonlaoded)
        {

            var last : Season? = null;

            for  (  elem in Elements)
            {

                if (elem.id == "stream")
                {

                    for (i in 1 until elem.getChild().children.size )
                    {
                        var item = elem.getChild().children[i].getChild();

                        //  var id = TvShow == null ? -1 : TvShow.id;

                        if (item.content == "Filme")
                        {
                            last =   Season(Title,"Specials", item.href ,   "0", TvShow);
                        }
                        else
                        {
                            Seasons.add(  Season(Title,"Season " + item.content, item.href , item.content , TvShow));
                        }
                    }
                    if (last != null)
                        Seasons.add(last);
                    Seasonlaoded = true;
                    break;
                }
            }




             LastSeason =if( LastSeason == 0)   Seasons.size - 1  else LastSeason - 1;
        }
    }
/*
    public void updateLastWatched()
    {
        var lstload = DataAccessLibrary.DataAccess.getLastSerie(Title);

        LastSeason = lstload.Item1;
        LastEpisode = lstload.Item2;
        LastSeason = LastSeason == 0 ? Seasons.Count - 1 : LastSeason - 1;
    }
*/

    var LastSeason = 0;

    var LastEpisode = 0;


 suspend  fun loadTv()
    {
        if (!Tvloaded)
        {

            if (TvShow == null && imdb != "")
            {
                //implemet get tV by imd
                    Log.d("imdb: ", imdb)
                var it = TMDB.getTVimdb(imdb).tv_results
                if(it.isNotEmpty())
                    TvShow = TMDB.getTV2(it[0].id.toString())
            }


            if (TvShow == null)
                TvShow = TMDB.GetTVShow(Title,productionStart);






            if (TvShow != null)
            {
                Tvloaded = true;

                backdrop = TMDB.ImgPath + TvShow?.backdropPath;
                despriction = TvShow?.overview ?: ""

                if (backdrop != TMDB.ImgPath)
                    BackdropMain =  (backdrop);
                else
                    BackdropMain =  (Start.Domain + backdropold);
            }
            if (TvShow == null)
            {
                BackdropMain =  Start.Domain + backdropold

            }

            if (despriction == "")
                despriction = olddesription;
        }

     //   var lstload = DataAccessLibrary.DataAccess.getLastSerie(Title);

   ///     LastSeason = lstload.Item1;
  //      LastEpisode = lstload.Item2;
    }


    override fun toString(): String {
        return  Title
    }

}


public class Season
{
    var Parser =   HtmlParser();
    val Name :String ;
    val   link : String;
    val  serie : String;
    var Episodes : MutableList<Episode> = mutableListOf() ;
    var season : String;
    var  tv : TvShow2? = null;
    var  tvid :Int  = -1;
    var Elements :  List<Element> = mutableListOf();
   constructor(  _serie :String,  name :String,   link :String,   season :String ,  tvid : TvShow2?)
    {
        serie = _serie;
        Name = name;
        this.link = link;
        this.season = season;
        if (tvid != null) {
            tv = tvid;
            this.tvid = tvid.id.toInt();
        }

    }

    var TvEpisodes :  List<Episode2> = mutableListOf()

    var loaded = false;

   suspend fun loadTV()
    {
        if(tv != null)
            TvEpisodes = TMDB.GetTvEpisode(tvid, season);
    }


    override fun toString(): String {
        return  Name
    }

    suspend fun load()
    {
        if (!loaded)
        {
            loaded = true;
            loadTV();


            Elements =   Parser.setup(Start.Domain + link);


            for (  item in Elements)
            {
                if (item.classname == "seasonEpisodeTitle")
                {
                    val data = item.getChild();
                    var title = data.getChild().content;
                    if(title == "")
                    {
                        title = data.children[1].content;
                    }
                    if(TvEpisodes.size > Episodes.size)
                        Episodes.add(  Episode(title, data.href , TvEpisodes[Episodes.size] ,TMDB.ImgPath+ tv?.backdropPath ));
                    else if(tv != null)
                        Episodes.add(  Episode(title, data.href, TMDB.ImgPath + tv?.backdropPath));
                    else
                    Episodes.add(  Episode(title, data.href));
                }
            }



         /*   for(  item in DataAccessLibrary.DataAccess.getWatchlistSerie(serie))
            {
                if(item.Item1.ToString() == season)
                {
                    Episodes[item.Item2].watched = true;
                    Episodes[item.Item2].watchtime = item.Item3;
                    Episodes[item.Item2].maxwatchtime = item.Item4;
                }
            }*/
        }
    }
}



public class Episode
{
    var Parser =   HtmlParser();
    var Title : String;
    var link : String;
    var  TvEpisode : Episode2? = null
    var Elements : List<Element> = mutableListOf();
    var  Hosters : MutableList<Hoster> = mutableListOf();

    var watched = false;
    var maxwatchtime = 0;
    var watchtime = 0;

    var defaultimg = "";


    val avaibleLanguage : HashSet<String>  =  HashSet<String>()
    fun   Description(): String
    {

            return TvEpisode?.overview ?: "";

    }



   fun Background() :String
    {


            return if( TvEpisode == null ||  TvEpisode!!.stillPath == "")   defaultimg else TMDB.ImgPath + TvEpisode!!.stillPath;

    }

   constructor(  title : String,   link : String )
    {
        Title = title;
        this.link = link;


    }
   constructor(  title : String ,   link : String ,   back : String  )
    {
        Title = title;
        this.link = link;
        this.defaultimg = back;

    }
    constructor(title : String, link : String, tv : Episode2,   back :String)
    {
        Title = title;
        this.link = link;
        this.TvEpisode = tv;
        this.defaultimg = back;
    }
    val  Languages : MutableList<DataLanguage> = mutableListOf();

    suspend fun load()
    {
        Elements =   Parser.setup(Start.Domain + link);

        for (  elem in Elements)
        {
            if (elem.classname == "row")
            {
                for (  item in elem.children)
                {
                    val lang = getLang(item.getCustomHeader("data-lang-key=\""));

                    if (!avaibleLanguage.contains(lang))
                        Languages.add(  DataLanguage(lang));
                    avaibleLanguage.add( lang);
                    Languages.find{  it.Name == lang}?.Hosters?.add(  Hoster(item.getChild().getChild().children[1].content, item.getCustomHeader("data-link-target=\""),lang));
                }
                break;
            }
        }

        for  (  lang in Languages)
        {
           lang.Hosters.sortBy {  if(  Start.Hoster.find { y ->    it.name ==  y } == null )  99 else  Start.Hoster.indexOf(Start.Hoster.find { y ->    it.name ==  y } ) }
        }
    }


    /*   public List<string> HosterStringList(string language)
       {
           List<string> list = new List<string>();

           foreach (var item in Hosters)
               if (item.Language == language)
                   list.Add(item.name);
           return list;
       }

       public List<Hoster> HosterList(string language)
       {
           List<Hoster> list = new List<Hoster>();

           foreach (var item in Hosters)
               if (item.Language == language)
                   list.Add(item);
           return list;
       }*/

    fun getLang(  id : String) : String = when (id) {
             "1" ->   "Deutsch";
              "2" -> "English";
              "3" -> "German Sub";

            else -> "No Data";


    }
}

 class DataLanguage
{
    var   Name : String;
    constructor(  lang : String)
    {
        Name = lang;
    }


    var Hosters : MutableList<Hoster>  = mutableListOf()

    override fun toString(): String {
        return Name
    }
}
 class Hoster
{
    var name : String = "";

    var   link : String = "";

    var Language : String = "";

    constructor(  _name :String,   _link : String ,   _language :String)
    {
        name = _name;
        link = _link;
        Language = _language;

    }

    override fun toString(): String {
        return name;
    }

}

