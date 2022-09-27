package io.fluffistar.ANiFLi.ui.SeriesPage

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import io.fluffistar.ANiFLi.Backend.EpisodeAdapter
import io.fluffistar.ANiFLi.Backendv2.Season
import io.fluffistar.ANiFLi.Backendv2.Serie
import io.fluffistar.ANiFLi.Backendv2.Start
import io.fluffistar.ANiFLi.R
import io.fluffistar.ANiFLi.Serializer.TvShow2
import kotlinx.coroutines.*


class SeriesPage : AppCompatActivity() {
         var  id : Long? = -1
        lateinit var img : ImageView
          lateinit var backbtn : MaterialButton
        lateinit var text  : TextView
        lateinit var  title : TextView
        lateinit var serie : Serie
        lateinit var comboseason : Spinner
        lateinit var episodelist : RecyclerView
        lateinit var cardView: MaterialCardView
          var tv : TvShow2? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = Firebase.analytics
        setContentView(R.layout.activity_series_page)
        setSupportActionBar(findViewById(R.id.toolbar))
        img = findViewById(R.id.img_SeriePage)
        text = findViewById(R.id.text_SeriePage)
        title = findViewById(R.id.title_SeriePage)
        comboseason = findViewById(R.id.seasoncombo)
        episodelist = findViewById(R.id.episodelist)
        cardView = findViewById(R.id.cardview_info_seriespage)
        id = intent.extras?.getLong("ID");
        backbtn = findViewById(R.id.backbtn_SeriePage)
       // Toast.makeText(this,"${id}",Toast.LENGTH_SHORT)
        episodelist.setLayoutManager( LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        episodelist.setHasFixedSize(true);
        episodelist.setItemViewCacheSize(20);
        episodelist.setDrawingCacheEnabled(true);
                SetupSerie()




        backbtn.setOnClickListener { this.finish() }
        backbtn.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus)
                backbtn.strokeColor =  getColorStateList( R.color.orange)
            else
                backbtn.strokeColor =  getColorStateList( R.color.maincolor)
        }



    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

    }



    fun createlist()   = runBlocking(context = Dispatchers.IO)  {

        serie!!.Seasons[comboseason.selectedItemId.toInt()].load()



    }


    var first = true


     fun SetupSerie(){



            serie = Start.SelectedSerie!!




       createlist()
         comboseason.setSelection(0)
                // blocking I/O operations

            Picasso.get().load(  serie.poster).into(img);
            title.text = serie.Title

            text.text =  serie.despriction



            comboseason.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                    createlist()
                    var adapter = EpisodeAdapter(
                    this@SeriesPage,
                    serie!!.Seasons[comboseason.selectedItemId.toInt()].Episodes, comboseason.selectedItemPosition)

                    episodelist.adapter  = adapter
                    if(first) {
                        episodelist.requestFocus()
                        first = false
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // your code here
                }
            }
                load()





    }

    fun load(){
        comboseason.adapter = ArrayAdapter<Season>(
                this ,
               R.layout.spinner_item,
                serie!!.Seasons
        )
    }
}