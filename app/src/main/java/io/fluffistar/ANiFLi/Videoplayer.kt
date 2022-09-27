package io.fluffistar.ANiFLi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import io.fluffistar.ANiFLi.Backend.Verwaltung
import io.fluffistar.ANiFLi.Backendv2.*
import kotlinx.coroutines.*


class Videoplayer : AppCompatActivity() {
    var  id : Long? = -1
    var season : Long ? = -1
    var serie : Serie? = null
    lateinit var _webview : WebView

   lateinit var _play : MaterialButton
  lateinit  var _top : MaterialCardView
  lateinit  var _bar : MaterialCardView
    lateinit  var _skipforward : MaterialButton
    lateinit   var _skipbackward : MaterialButton
    lateinit   var _backbtn : MaterialButton
    lateinit   var _title : TextView
    lateinit   var _next : MaterialButton
    lateinit    var _maxtime : TextView
    lateinit   var _curtime : TextView
    lateinit    var _languagecombo : Spinner
    lateinit  var _hostercombo : Spinner
    lateinit var _timeline : SeekBar
    lateinit var _more : MaterialButton



    var SelectedEpisodes :Int
        get () =  Start.SelectedSerie!!.LastEpisode
        set  (value)  { Start.SelectedSerie!!.LastEpisode = value }
    var SelectedSeason : Int
        get() =  Start.SelectedSerie!!.LastSeason
        set(value) { Start.SelectedSerie!!.LastSeason = value;  }


    class MyJavaScriptInterface(val activity: Activity) {
        @SuppressWarnings("unused")
        @JavascriptInterface
        fun processHTML(html: String?) {
            Log.d("MYHTML", html!!)

            when(hoster){

                "Vivo" -> {
                    var src = vivo(html)
                    Log.d("MYSRC", src)
                    if (src != "")
                        if (!loaded) {
                            loaded = true
                            Log.d("MYSRC", src)
                            activity.runOnUiThread {
                                _video.stopPlayback()
                                src = java.net.URLDecoder.decode(src, "UTF-8");
                                _video.setVideoURI(Uri.parse(src))

                                myurl = src

                            }
                        }
                }
                "VOE" -> {
                    var src = veo(html)
                    Log.d("MYSRC", src)
                    if (src != "")
                        if (!loaded) {
                            loaded = true
                            Log.d("MYSRC", src)
                            activity.runOnUiThread {
                                _video.stopPlayback()
                                src = java.net.URLDecoder.decode(src, "UTF-8");
                                _video.setVideoURI(Uri.parse(src))
                                myurl = src

                            }
                        }
                }
                "Streamtape" -> {
                    var src = streamtape(html)
                    Log.d("MYSRC", src)
                    if (src != "")
                        if (!loaded) {
                            loaded = true
                            Log.d("MYSRC", src)
                            activity.runOnUiThread {
                                _video.stopPlayback()
                                src = java.net.URLDecoder.decode(src, "UTF-8");
                                _video.setVideoURI(Uri.parse(src))
                                myurl = src

                            }
                        }
                }
                "Vidoza" -> {
                    var src = vidoza(html)
                    Log.d("MYSRC", src)
                    if (src != "")
                        if (!loaded) {
                            loaded = true
                            Log.d("MYSRC", src)
                            activity.runOnUiThread {
                                _video.stopPlayback()

                                _video.setVideoURI(Uri.parse(src))

                                myurl = src


                            }
                        }
                }

            }

        }}



    lateinit var mFirebaseAnalytics : FirebaseAnalytics

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        super.onKeyUp(keyCode, event)
        set_visible()
         when(keyCode){
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ->playbtn_click()
             KeyEvent.KEYCODE_BACK -> back()
             KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> skip_forward()
             KeyEvent.KEYCODE_MEDIA_REWIND -> skip_backwards()
            // KeyEvent.KEYCODE_MEDIA_NEXT -> if (loaded) next()
         }
        return true
    }

    fun playbtn_click(){
        if(_video.isPlaying) {
            _video.pause()
            _play.setIconResource(R.drawable.ic_play)
            count = 0
        }
        else {
            _video.start()
            _play.setIconResource(R.drawable.ic_pause)
            count = 0
        }
    }

    fun set_visible(){
        _bar.visibility = RelativeLayout.VISIBLE
        _top.visibility = RelativeLayout.VISIBLE
        count = 0
    }

    fun skip_forward(){
        _video.seekTo(_video.currentPosition + 10000)
        _video.start()
        count = 0
    }

    fun skip_backwards(){
        _video.seekTo(_video.currentPosition - 10000)
        _video.start()
        count = 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          mFirebaseAnalytics = Firebase.analytics
        setContentView(R.layout.activity_videoplayer)
    //    setSupportActionBar(findViewById(R.id.toolbar))

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.


        serie = Start.SelectedSerie





        if(serie != null) {


            _webview = findViewById(R.id.webview)

            _top = findViewById(R.id.topbar)
            _bar = findViewById(R.id.bottombar)
            _video = findViewById(R.id.videoplayer2)
            _play = findViewById(R.id.playbtn)
            _skipbackward = findViewById(R.id.skipbackward)
            _skipforward = findViewById(R.id.skipforward)
            _backbtn = findViewById(R.id.backbtn)
            _title = findViewById(R.id.title_videoplayer)
            _next = findViewById(R.id.nextbtn)
            _maxtime = findViewById(R.id.secondtext)
            _curtime = findViewById(R.id.firsttext)
            _languagecombo = findViewById(R.id.languagecombo)
            _hostercombo = findViewById(R.id.Hostercombo)
            _timeline = findViewById(R.id.seekbar)
            _more = findViewById(R.id.openwith)

            val webSettings: WebSettings = _webview.settings
            _webview.addJavascriptInterface(MyJavaScriptInterface(this), "HTMLOUT")
            webSettings.javaScriptEnabled = true
            _webview.webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    _webview.visibility = View.GONE
                }

                override fun onPageFinished(view: WebView, url: String) {
                    Log.d("MYHTML", _webview.url.toString())

                    loaded = false
                    if(!_webview.url!!.contains(Start.Domain)) {
                        _webview.visibility = View.GONE
                        _webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                    }else{
                        _webview.visibility = View.VISIBLE


                    }

                }
            }

            _next.setOnClickListener { if (loaded) next() }


            _backbtn.setOnClickListener {

                back()

            }




            _top.setOnClickListener {
                set_visible()
            }
            _bar.setOnClickListener {
                set_visible()
            }
            _video.setOnClickListener {
                set_visible()
            }
            _skipforward.setOnClickListener {
                skip_forward()
            }
            _skipbackward.setOnClickListener {
               skip_backwards()
            }

            _play.setOnClickListener {

                playbtn_click()
            }

            _timeline.keyProgressIncrement = 10

            _languagecombo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    languageupdate()

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here

                }
            }
            _hostercombo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hosterupdate()

                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here

                }
            }

            _timeline.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser) {
                      //  _video.pause()
                          count = 0
                        _video.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    count = 0
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    count = 0
                }


            })

            _video.setOnPreparedListener {  total_duration = _video.duration
                _maxtime.text = timeConversion(total_duration.toLong())
                _curtime.text = timeConversion(current_pos.toLong())
                _timeline.max = total_duration
                try {
                    _video.start()
                }catch (ex: Exception){
                    val intent = Intent()
                    intent.action =  Intent.ACTION_VIEW

                    intent.setDataAndType(Uri.parse(myurl), "video/*")

                   startActivity(Intent.createChooser(intent, "Select a Player"))
                }
                _play.setIconResource(R.drawable.ic_pause)
                _video.setOnCompletionListener { if (loaded) next() }


            }

            _more.setOnClickListener {
                val intent = Intent()
                intent.action =  Intent.ACTION_VIEW

                intent.setDataAndType(Uri.parse(myurl), "video/*")

             startActivity(Intent.createChooser(intent, "Select a Player"))
            }

         setup(serie)

        }
    }
    private var selectedepisdoe : Episode? = null



    fun init(){


        selectedepisdoe  = Start.SelectedSerie!!.Seasons[SelectedSeason].Episodes[SelectedEpisodes]

        val job: Job = GlobalScope.launch(context = Dispatchers.Default) {
            selectedepisdoe!!.load()
        }
        job.start()
        while(!job.isCompleted){}
        //   current_pos = _video.currentPosition

        _title.text = if(selectedepisdoe!!.Title.length > 30) selectedepisdoe!!.Title.substring(0,30)+"..."  else selectedepisdoe!!.Title



        _languagecombo.adapter = ArrayAdapter<DataLanguage>(this, R.layout.spinner_item, selectedepisdoe!!.Languages)

                _hostercombo.adapter = ArrayAdapter<Hoster>(this, R.layout.spinner_item, selectedepisdoe!!.Languages[0].Hosters)


                _hostercombo.setSelection(0)
                _languagecombo.setSelection(0)
                //  languageupdate()

        //display video duration


        updateinit()
    }



    fun hosterupdate(){

        if (_hostercombo.size > 0) {
            loaded = false
            CookieManager.getInstance().setCookie(
                    Start.Domain,
                    Start.Session
            );


            hoster = hosterlist[_hostercombo.selectedItemPosition].name
            Thread{Start.addwatch(Start.SelectedSerie!!, this)}.start()
            _webview.loadUrl(Start.Domain + (hosterlist[_hostercombo.selectedItemPosition].link))
        }
    }

    var hosterlist : List<Hoster> = mutableListOf()

    fun languageupdate(){
        if(_languagecombo.size > 0){
            loaded = false
            val x = _languagecombo.selectedItemPosition
            hosterlist = selectedepisdoe!!.Languages[x].Hosters
            _hostercombo.adapter = ArrayAdapter<Hoster>(this, R.layout.spinner_item, hosterlist)

            _hostercombo.setSelection(0) ;
        }
    }
    var combo_open : Boolean = false
    var count : Int = 0
    fun updateinit(){

        Thread {



            try {
             runOnUiThread() {
                    if(_video.isPlaying) {
                        //does actions on Ui-Thread u neeed it because Ui-elements can only be edited in Main/Ui-Thread
                        count++
                        current_pos = _video.currentPosition
                        _curtime.text = timeConversion(current_pos.toLong())
                        _timeline.progress = current_pos
                        if (count > 6  ) {
                            _bar.visibility = RelativeLayout.GONE
                            _top.visibility = RelativeLayout.GONE
                        }

                    }
                }

            } catch (ed: IllegalStateException) {
                ed.printStackTrace()
            }

            Thread.sleep(500)

            updateinit()

        }.start()

    }
    fun back(){


        finish()

    }
    fun finished(html: String){
        var src = ""
            _video.setVideoURI(Uri.parse(src))
         _video.start()
        _play.setIconResource(R.drawable.ic_pause)

    }
    fun play(src: String){
       _video.setVideoURI(Uri.parse(src))
        _video.start()
        _play.setIconResource(R.drawable.ic_pause)
    }

    fun setup(serie: Serie?){

        init()

    }
    fun next(){
        loaded = false

        if (SelectedEpisodes+1 < Start.SelectedSerie!!.Seasons[SelectedSeason].Episodes.size )
        {
            SelectedEpisodes++;
            init()
        }
        else
        {
            if (SelectedSeason + 1 < Start.SelectedSerie!!.Seasons.size )
            {
                if (Start.SelectedSerie!!.Seasons[SelectedSeason + 1].Name != "Special" || Verwaltung.Settings.autplay)
                {
                    SelectedSeason++;
                    SelectedEpisodes = 0;
                    init()
                }
                else
                    back()
//else do something else like Series finished or so


            } else
                back()
        }
    }
    fun timeConversion(value: Long): String? {
        val songTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        songTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return songTime
    }
    private var current_pos : Int = 0
    private var total_duration : Int =0
    companion object{
        var loaded = false ;
        var hoster = ""
        var myurl = ""
        @SuppressLint("StaticFieldLeak")
        lateinit var _video : VideoView





        fun veo(str: String) : String  = runBlocking{
            val parser = HtmlParser()

            var Elements: List<Element>
            var found = false;

            Elements=   parser.setup_HTML(str);

            for(  i in 0 until Elements.size )
            {
                if(Elements[i].id == "voe-player")
                {
                    found = true;
                }

                if(found && Elements[i].type == io.fluffistar.ANiFLi.View.script )
                {

                    val s = Elements[i].content.getBetween("hls\": \"", "\"");

                    return@runBlocking s;
                }
            }


            return@runBlocking "";

        }

        fun vidoza(str: String) : String = runBlocking{
            val parser = HtmlParser()

            var Elements: List<Element>


            Elements=   parser.setup_HTML(str);

            for(  i in Elements.indices)
            {
                if(Elements[i].id == "player_html5_api")
                {
                  return@runBlocking Elements[i].src
                }


            }


            return@runBlocking "";

        }
        fun streamtape(str: String) : String = runBlocking{
            val parser = HtmlParser()


            val Elements: List<Element> = parser.setup_HTML(str);

            for(  i in Elements.indices)
            {
                if(Elements[i].id == "videojs_html5_api")
                {
                    return@runBlocking "https:" + Elements[i].src.replace("amp;", "");
                }


            }


            return@runBlocking "";

        }
        fun vivo(str: String) : String = runBlocking{
           var output = str.getBetween("source: '", "',");

            return@runBlocking vivolink(output);
        }

        fun vivolink(str: String):String{


            var input : String = str
            input = input.replace("%5E", "-");
            input = input.replace("i", ":");


            input = input.replace("-", "/");

            // input = input.replace("G%405", "vod");
            input = input.replace("%5D", ".");
            input = input.replace("%40", "o");
            input = input.replace("%5C", "-");


            input = input.replace("%3A", "i");
            input = input.replace("%3F", "n");
            input = input.replace("%3D", "l");


            input = input.replace("%2A", "Y");
            input = input.replace("%2B", "Z");
            input = input.replace("%29", "X");
            input = input.replace("%28", "W");
            input = input.replace("%24", "S");
            input = input.replace("%22", "Q");
            input = input.replace("%21", "P");
            input = input.replace("%26", "U");
            input = input.replace("%3B", "j");
            input = input.replace("%7D", "N");

            input = input.replace("%27", "V");
            input = input.replace("%3C", "k");
            input = input.replace("%7E", "O");
            input = input.replace("%7C", "M");
            input = input.replace("%7B", "L");
            input = input.replace("%25", "T");
            input = input.replace("%3E", "m");
            input = input.replace("%23", "R");
            input = Swap(input, "F", "u");
            input = input.replace("%60", "1");
            input = Swap(input, "I", "x");
            input = Swap(input, "D", "s");
            input = Swap(input, "C", "r");
            input = Swap(input, "A", "p");
            input = Swap(input, "K", "z");
            input = Swap(input, "y", "J");
            input = Swap(input, "B", "q");
            input = Swap(input, "_", "0");
            input = Swap(input, "E", "t");
            input = Swap(input, "2", "a");
            input = Swap(input, "H", "w");
            input = Swap(input, "G", "v");
            input = Swap(input, "b", "3");
            input = Swap(input, "d", "5");
            input = Swap(input, "e", "6");
            input = Swap(input, "f", "7");
            input = Swap(input, "g", "8");
            input = Swap(input, "h", "9");
            input = Swap(input, "c", "4");
            Log.d("URL==2", input)
            return  input
        }
        fun Swap(main: String, a: String, b: String): String {
            var x = main
            val p: String = "~"
            x = x.replace(a, p)
            x = x.replace(b, a)
            x = x.replace(p, b)
            return x
        }

    }
}