package io.fluffistar.ANiFLi.ui.Search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.fluffistar.ANiFLi.Backend.SerienAdapter
import io.fluffistar.ANiFLi.Backendv2.Start
import io.fluffistar.ANiFLi.R
import io.fluffistar.ANiFLi.calculateNoOfColumns
import io.fluffistar.ANiFLi.pmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment() {

    private lateinit var searchtxt: TextInputEditText
    private lateinit var searchbtn: MaterialButton
    private lateinit var epilist : RecyclerView
    private lateinit var spinner : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_search, container, false)

        searchtxt = root.findViewById(R.id.searchbox)
        searchbtn = root.findViewById(R.id.searchbtn)
        epilist = root.findViewById(R.id.searchepilist)
        spinner = root.findViewById(R.id.progressBar)
        val mNoOfColumns: Int = calculateNoOfColumns(requireActivity().applicationContext,120f)
        epilist.setLayoutManager(GridLayoutManager(requireContext(), mNoOfColumns))
        val searchlist :MutableList<String> = mutableListOf()
        var lastsearch = ""
        for(i in Start.Allseries)
            searchlist.add(i.Title)

       // searchtxt
            //.setAdapter(ArrayAdapter<String>(root.context, R.layout.spinner_item, searchlist))

       // searchtxt.requestFocus()
        var focused = false
        searchtxt.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus)
                focused = true
            else if(focused && !hasFocus && searchtxt.text.toString() != lastsearch) {
                searchbtn.callOnClick()
                focused = false
                lastsearch = searchtxt.text.toString()
            }



        }
        searchbtn.setOnClickListener {
            spinner.visibility = View.VISIBLE
GlobalScope.launch(Dispatchers.IO) {

            Log.d("clicl", "clicked")
            var list = Start.Allseries.filter { it.Title.contains(
                searchtxt.text.toString(),
                ignoreCase = true
            ) }.take(100)
            Log.d("COUNT", list.size.toString())

            list.pmap {
                it.load()

            }

    withContext(Dispatchers.Main) {
        var ad = SerienAdapter(requireContext(), list)
        epilist.adapter = ad;
        spinner.visibility = View.GONE
        epilist.requestFocus()
    }}

        }

        return root
    }


}