package io.fluffistar.ANiFLi.Backend



import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.squareup.picasso.Picasso
import io.fluffistar.ANiFLi.R
import io.fluffistar.ANiFLi.Serializer.Serie
import io.fluffistar.ANiFLi.ui.SeriesPage.SeriesPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class CustomAdapter(context: Context, resource: Int, objects: List<Serie>) : ArrayAdapter<Serie?>(context, resource, objects) {
    var items_list: List<Serie> = ArrayList()
    var custom_layout_id: Int
    override fun getCount(): Int {
        return items_list.size
    }

    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(custom_layout_id, null)
        }

        // initializing the imageview and textview and
        // setting data
        val imageView: ImageView = v!!.findViewById(R.id.img_Serie)
        val textView = v.findViewById<TextView>(R.id.text_Serie)

        // get the item using the  position param
        val item: Serie = items_list[position]
        GlobalScope.launch(Dispatchers.Unconfined) {
            item.load()
            Log.d("Cover ${item.name}",item.cover)
            withContext(Dispatchers.Main) {
                Picasso.get().load(item.cover).into(imageView);
                textView.text = (if (item?.name!!.length <= 16) item.name else item.name?.substring(
                    0,
                    16
                ) + "...")

            }}

        v.setOnClickListener{
            Verwaltung.SelectedSerie = item
            val intent = Intent(
                    context,
                    SeriesPage::class.java
            )

            context.startActivity(intent)

        }


        return v
    }

    init {
        items_list = objects
        custom_layout_id = resource
    }
}