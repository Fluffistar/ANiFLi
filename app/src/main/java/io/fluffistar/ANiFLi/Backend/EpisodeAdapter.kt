package io.fluffistar.ANiFLi.Backend

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import io.fluffistar.ANiFLi.Backendv2.Episode
import io.fluffistar.ANiFLi.Backendv2.Start
import io.fluffistar.ANiFLi.R
import io.fluffistar.ANiFLi.Videoplayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EpisodeAdapter(private val context: Context, val ArrayList: MutableList<Episode>, val season : Int ) :
    RecyclerView.Adapter<EpisodeAdapter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.episode_cardview, parent, false)
        return Viewholder(view)
    }
    private var focusedPosition = 0



    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val model : Episode =  ArrayList[position]
        GlobalScope.launch(Dispatchers.IO) {
            //   model.load()

            withContext(Dispatchers.Main) {

                    holder.Title.text = "${model.Title }"

                    holder.text.text = if(model.Description().length < 140) model.Description() else model.Description().substring(0,140) + "..."


                try {

                       Picasso.get().load(TMDB.ImgPath + model.Background()).noPlaceholder().noFade().into(holder.img)
                } catch (ex: Exception) {
                    Log.d("ERROR", ex.message.toString())
                }



            }
        }

        var card : MaterialCardView = (holder.itemView as MaterialCardView)

        holder.itemView.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus)
                card.strokeColor = ContextCompat.getColor(context,R.color.orange)
            else
                card.strokeColor = ContextCompat.getColor(context,R.color.maincolor)

        }
        holder.itemView.setOnClickListener {
            // Toast.makeText(context, "${s.id}", Toast.LENGTH_SHORT).show()

            val intent = Intent(
                context,
                Videoplayer::class.java
            )


            Start.SelectedSerie!!.LastEpisode = position

            Start.SelectedSerie!!.LastSeason = season

            startActivity(context,intent,null)

        }


    }




    override fun getItemCount(): Int {

        return  ArrayList.size
    }


    override fun getItemId(position: Int): Long {
        return  position as Long;
    }

        inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Title: TextView
        val img : ImageView
        val text: TextView

        init {
            text = itemView.findViewById(R.id.text_Episode)
            Title = itemView.findViewById(R.id.title_Episode)
            img = itemView.findViewById(R.id.img_episode)

        }
    }


}