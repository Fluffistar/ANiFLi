package io.fluffistar.ANiFLi.Backend

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.fluffistar.ANiFLi.R
import io.fluffistar.ANiFLi.Serializer.Serie
import io.fluffistar.ANiFLi.ui.SeriesPage.SeriesPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*

 */
class CustomAdapterRecycler(private val context: Context ,private val dataSet: List<Serie> ) :
    RecyclerView.Adapter<CustomAdapterRecycler.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val image : ImageView
        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.text_Serie)
            image = view.findViewById(R.id.img_Serie)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // Create a new view, which defines the UI of the list item
        val view =inflater
            .inflate(R.layout.sample_serie_v_i_e_w, viewGroup, false)


        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val item = dataSet[position]
        GlobalScope.launch(Dispatchers.Unconfined) {
        item.load()
            Log.d("Cover ${item.name}",item.cover)
            withContext(Dispatchers.Main) {
                viewHolder.textView.text =
                    (if (item?.name!!.length <= 16) item.name else item.name?.substring(
                        0,
                        16
                    ) + "...")
                Picasso.get().load(item?.cover).into(viewHolder.image);
            }
        }




     viewHolder.itemView.setOnClickListener{

            val intent = Intent(
                this.context,
                SeriesPage::class.java
            )
            item.loadSeasons()

            Verwaltung.SelectedSerie =item

            this.context.startActivity(intent)

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}