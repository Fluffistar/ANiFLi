package io.fluffistar.ANiFLi

import android.content.Context
import android.util.DisplayMetrics
import kotlinx.coroutines.async

import kotlinx.coroutines.runBlocking


fun <A, B>List<A>.pmap(f: suspend (A) -> B): List<B> = runBlocking {
    map { async  { f(it) } }.map { it.await() }
}

fun calculateNoOfColumns(
    context: Context,
    columnWidthDp: Float
): Int { // For example columnWidthdp=180
    val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / columnWidthDp + 0.5).toInt()
}

public fun String.getBetween (   getStart :String,   getEnd : String) : String
{
    val s = this
    var startpos = 0;
    var found = false;

    var i = 0 ;
    while(i <  s.length )
    {
        if(s[i] == getStart[0] && (i + getStart.length) < s.length)
        {
            if (s.substring(i, i + getStart.length) == getStart)
            {
                startpos = i + getStart.length;
                i = startpos;
                found = true;
            }
        }

        if(found)
            if ( s[i] == getEnd[0] && (i + getEnd.length) < s.length)
            {
                if (s.substring(i,i+ getEnd.length) == getEnd)
                {
                    return s.substring(startpos, i );
                }

            }
    i++;
    }
    return "";
}