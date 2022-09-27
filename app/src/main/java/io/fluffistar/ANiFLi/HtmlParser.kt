package io.fluffistar.ANiFLi

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL


enum class  View
{
    body, div, a, h, p, img, video, ul, span, li, h1, h2, h3, tab, nav, input, button, form, section, label, article, style, b, code, iframe, script, option, path, svg, ol, em, unknown
}

class HtmlParser {
    var Elements: List<Element> = mutableListOf()

      suspend fun  setup(  url : String) :List<Element> = withContext(Dispatchers.IO)
    {

        val reuest = URL(url)
        val conn: HttpURLConnection = reuest.openConnection() as HttpURLConnection // create a connection
        val html = conn.inputStream.bufferedReader().use(BufferedReader::readText)

        Log.d("Html: " , html)

        Elements = parse(html, "<", "</", ">", "/>");
        return@withContext Elements;
    }

      suspend fun setup_HTML(  html : String) : List<Element> = withContext(Dispatchers.IO)
    {



        Elements = parse(html, "<", "</", ">", "/>");
        return@withContext Elements;
    }

    private fun parse(  data : String ,   begin : String,   seperator1 : String,   seperator2 : String,  seperator3 : String) :List<Element>
    {


        val whitelist = arrayListOf<String>( "!", "img", "input", "meta", "link", "br", "area", "base", "col", "command", "embed", "hr", "keygen", "param", "source", "track", "wbr", "!Doctype" )
        val len1 = seperator1.length;
        val len2 = seperator2.length;
        val len3 = seperator3.length;
        val beginlen = begin.length;
        val output : MutableList<Element> = mutableListOf()
        var sep = false;
        var counter = -1;
        var lastpos = 0;
        val lastpositions : MutableList<Int> =   mutableListOf()
        val itempos: MutableList<Int> =   mutableListOf()
        var ignore = false;

        var seppos = -1;


        //	std::vector<int>  hendpositions;

        for ( i in 0 until (data.length) )
        {

            if (!ignore && data[i] == begin[0])
            {
                if (data.substring(i, i+beginlen) == begin && data.substring(i,i+ len1) != seperator1)
                {
                    lastpositions.add(i);
                    counter++;

                }
            }

            if (lastpositions.size > 0     && !ignore && !sep && data[i] == seperator2[0])
            {
             //   Log.d("Pos: ", data.substring(i,i+ len2))
             //   Log.d("LEnm: ", i.toString())
                if (data.substring(i,i+ len2) == seperator2)
                {
                    var pos = (i + len2);
                    //		hendpositions.push_back(i);
                    lastpos = lastpositions[lastpositions.size - 1];

                    var parentid = -1;
                    var position = output.size;
                    if (position > 0)
                    {
                        var lastitem = output[output.size - 1];
                        parentid = if(lastitem.dispostufe == counter)   lastitem.parent else parentid;
                        parentid = if(lastitem.dispostufe < counter)   output.size - 1 else parentid;
                        while (lastitem.dispostufe > counter)
                        {
                            lastitem = output[lastitem.parent];
                            parentid = lastitem.parent;

                        }


                    }


                    output.add(  Element(data.substring(lastpos, pos  ), counter, parentid, position));

                    if (parentid != -1)
                    {
                        output[parentid].children.add(output[position]);
                    }

                    lastpositions[lastpositions.size - 1] = i + 1;
                    itempos.add(output.size - 1);
                    for (  item in whitelist)
                    {
                        if (data.substring(lastpos + 1 , lastpos + 1 +item.length) == item)
                        {
                            /*	lastpos = lastpositions.back() ;
                                lastpositions.pop_back() ;
                                    counter--;*/
                            sep = true;
                        }

                    }

                    val igno = "script";
                    if (data.substring(lastpos + 1,lastpos +1 + igno.length) == igno)
                    {
                        ignore = true;
                    }

                }
            }

            if (sep && data[i] == seperator2[0])
            {
                if (data.substring(i,i+ len2) == seperator2)
                {

                    /*	if(counter > 1){
                            counter--;
                            sep = false ;
                             continue;
                        }*/

                    val pos = i; //(i + len2);
                    lastpos = lastpositions[lastpositions.size - 1];
                    lastpositions.removeAt(lastpositions.size - 1);
                    if (seppos > lastpos)
                    {
                        var stri = data.substring(lastpos, seppos  );
                        var itempos1 = itempos[itempos.size - 1];
                        itempos.removeAt(itempos.size - 1);

                        output[itempos1].content = (stri);
                    }
                    else
                    {
                        var itempos1 = itempos[itempos.size - 1];
                        itempos.removeAt(itempos.size - 1);
                    }
                    //		std::cout << lastpos << "::" << pos << " Output: " << stri << std::endl;




                    ignore = false;
                    lastpos = pos + 1;
                    counter--;
                    sep = false;
                }
            }


            if (data[i] == seperator3[0])
            {
                if (data.substring(i, i + len3) == seperator3)
                {
                    /*		if(counter > 1){
                                counter--;
                                sep = false ;
                                 continue;
                            }*/

                    lastpos = lastpositions[lastpositions.size - 1];
                    lastpositions.removeAt(lastpositions.size - 1);
                    //	int pos = (i + len3);
                    var pos = i;
                    var stri = data.substring(lastpos, pos  );

                    //std::cout << lastpos << "::" << pos << " Output: " << stri << std::endl;


                    var itempos1 = itempos[itempos.size - 1];
                    itempos.removeAt(itempos.size - 1);

                    //		output[itempos1].content = (stri);


                    ignore = false;
                    lastpos = pos + 1;
                    counter--;
                    sep = false;
                }
            }

            if (counter >= 0 && data[i] == seperator1[0])
            {
                if (data.substring(i,i+ len1) == seperator1)
                { //first char equals
                    sep = true;
                    seppos = i;
                }
            }
        }

        return  output;
    }
}

class Element{
    private val seperator_src: String = "src=\""
    private val seperator_id: String = "id=\""
    private val seperator_class: String = "class=\""
    private val seperator_href: String = "href=\""
    private var src_split: Boolean = false
    private var id_split: Boolean = false
    private var class_split: Boolean = false
    private var href_split: Boolean = false
      var src: String = ""
      var id: String = ""
      var classname: String = ""
      var href: String = ""
    private val title: String = ""
    private var srcpos = 0
    private var idpos = 0
    private var classpos = 0
    private var hrefpos = 0
    private var typedone: Boolean = false
    var pos = -1

    var content: String = ""
    var header: String = ""
    var dispostufe = 0
    var parent = 0
    lateinit var type : View
    var children: MutableList<Element> = mutableListOf()

    fun getCustomHeader(item: String): String {
        var custompos = 0
        var custombool: Boolean = false
        var i = 1
        while (i < header.length) {
            if (header[i] == item[0] && i + item.length < header.length) {
                try {
                    if (header.substring(i, i + item.length) == item) {
                        custompos = i + item.length
                        i = custompos
                        custombool = true
                    }
                } catch (ex: Exception) {
                }
            }
            if (custombool && header[i] == '\"') {
                return header.substring(custompos, i  )
            }
            i++
        }
        return ""
    }
    fun getChild() : Element{
        return children[0];
    }

    constructor(_header: String, _dispo: Int, _parent: Int, _pos: Int) {
        header = _header
        dispostufe = _dispo
        parent = _parent
        pos = _pos
        var i = 1
        while (i < header.length) {
            if (!typedone && (header[i] == ' ' || header[i] == '>')) {
                val typestr: String = header.substring(1, i  )
                type = hashit(typestr)
                typedone = true
            }
            //src
            if (header[i] == seperator_src[0] && i + seperator_src.length < header.length) {
                try {
                    if (header.substring(i,i+ seperator_src.length) == seperator_src) {
                        srcpos = i + seperator_src.length
                        i = srcpos
                        src_split = true
                    }
                } catch (ex: java.lang.Exception) {
                    Log.d("Error: ", ex.message.toString())
                }
            }
            if (src_split && header[i] == '"') {
                src = header.substring(srcpos, i  )
                src_split = false
            }
            //id
            if (header[i] == seperator_id[0] && i + seperator_id.length < header.length) {
                try {
                    if (header.substring(i, i+ seperator_id.length) == seperator_id) {
                        idpos = i + seperator_id.length
                        i = idpos
                        id_split = true
                    }
                } catch (ex: java.lang.Exception) {
                    Log.d("Error: ", ex.message.toString())
                }
            }
            if (id_split && header[i] == '\"') {
                id = header.substring(idpos, i  )
                id_split = false
            }


            //class
            if (header[i] == seperator_class[0] && i + seperator_class.length < header.length) {
                try {
              //      Log.d("Header: ", "TRUE"+i)
                    if (header.substring(i, i+  seperator_class.length) == seperator_class) {
               //         Log.d("Header: ", "TRUE2 "+i)
                        classpos = i + seperator_class.length
                        i = classpos
                        class_split = true
                    }
                } catch (ex: java.lang.Exception) {
                    Log.d("Error: ", ex.message.toString())
                }
            }
            if (class_split && header[i] == '"') {
                classname = header.substring(classpos, i  )
                class_split = false
            }
            //href
            if (header[i] == seperator_href[0] && i + seperator_href.length < header.length) {
                if (header.substring(i, i+ seperator_href.length) == seperator_href) {
                    hrefpos = i + seperator_href.length
                    i = hrefpos
                    href_split = true
                }
            }
            if (href_split && header[i] == '"') {
                href = header.substring(hrefpos, i  )
                href_split = false
            }
            i++
        }
    }


    fun hashit(inString: String): View {
        if (inString == "div") {
            return View.div
        }
        if (inString == "body") {
            return View.body
        }
        if (inString == "a") {
            return View.a
        }
        if (inString == "h") {
            return View.h
        }
        if (inString == "p") {
            return View.p
        }
        if (inString == "img") {
            return View.img
        }
        if (inString == "video") {
            return View.video
        }
        if (inString == "li") {
            return View.li
        }
        if (inString == "ul") {
            return View.ul
        }
        if (inString == "span") {
            return View.span
        }
        if (inString == "h2") {
            return View.h2
        }
        if (inString == "h3") {
            return View.h3
        }
        if (inString == "tab") {
            return View.tab
        }
        if (inString == "nav") {
            return View.nav
        }
        if (inString == "input") {
            return View.input
        }
        if (inString == "button") {
            return View.button
        }
        if (inString == "form") {
            return View.form
        }
        if (inString == "section") {
            return View.section
        }
        if (inString == "label") {
            return View.label
        }
        if (inString == "article") {
            return View.article
        }
        if (inString == "style") {
            return View.style
        }
        if (inString == "b") {
            return View.b
        }
        if (inString == "code") {
            return View.code
        }
        if (inString == "iframe") {
            return View.iframe
        }
        if (inString == "script") {
            return View.script
        }
        if (inString == "option") {
            return View.option
        }
        if (inString == "path") {
            return View.path
        }
        if (inString == "svg") {
            return View.svg
        }
        if (inString == "ol") {
            return View.ol
        }
        if (inString == "em") {
            return View.em
        }
        return if (inString == "h1") {
            View.h1
        } else View.unknown
    }
}