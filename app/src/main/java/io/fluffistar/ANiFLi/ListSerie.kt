package io.fluffistar.ANiFLi

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.fluffistar.ANiFLi.Backend.SerienAdapter
import io.fluffistar.ANiFLi.Backendv2.Serie

import kotlinx.coroutines.*

/**
 * TODO: document your custom view class.
 */
class ListSerie : LinearLayout {

     private var _panel : RecyclerView
     private  var _text : TextView

    constructor(context: Context ,  attrs : AttributeSet):super(context , attrs){

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



        inflater.inflate(R.layout.sample_list_serie,  this )
        _panel = findViewById(R.id.list_Serie)
        _text = findViewById(R.id.list_text)

    }

    lateinit var adapter : SerienAdapter

    fun Update(){
        adapter.notifyDataSetChanged()
    }

    fun load (it :  Serie) = runBlocking {



    }

    constructor(context: Context, list : List<Serie>, string: String   ):super(context  )  {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater




    val  root =  inflater.inflate(R.layout.sample_list_serie, this)
        _panel = findViewById(R.id.list_Serie)
        _text = findViewById(R.id.list_text)
        root.isEnabled = false
Thread{
            list.pmap {
                it.load()
                (context as Activity).runOnUiThread {
                    Update()
                    root.isEnabled = true
                }

            }}.start()


        (context as Activity).runOnUiThread {
            adapter = SerienAdapter(context, list)


            _panel.setLayoutManager(
                LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            );

                _panel.adapter = adapter
                _panel.setHasFixedSize(true);
                _panel.setItemViewCacheSize(20);
                _panel.setDrawingCacheEnabled(true);
                _text.text = string

}





        }



}

