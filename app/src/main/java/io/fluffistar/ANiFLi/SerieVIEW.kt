package io.fluffistar.ANiFLi


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.squareup.picasso.Picasso


/**
 * TODO: document your custom view class.
 */
class SerieVIEW : RelativeLayout  {


    private  var  _img :ImageView
    private  var _text : TextView
    private var _id : Long = -1


    fun setImage(value : String){

     Picasso.get().load(value).into(_img);

    }

    var id : Long?
        get() = _id
        set(value) {
            _id = value!!

        }

    var text : String?
        get() = _text.text.toString()
        set(value) {
            _text.text = value
        }

    constructor(context: Context ,  attrs : AttributeSet):super(context , attrs){

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



        inflater.inflate(R.layout.sample_serie_v_i_e_w,  this )
        _text = findViewById(R.id.text_Serie)

        _img = findViewById(R.id.img_Serie)
    }
    constructor(context: Context  ):super(context  ){

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



        inflater.inflate(R.layout.sample_serie_v_i_e_w,  this )
        _text = findViewById(R.id.text_Serie)

        _img = findViewById(R.id.img_Serie)
    }



}