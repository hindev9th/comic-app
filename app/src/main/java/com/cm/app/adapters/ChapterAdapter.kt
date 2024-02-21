package com.cm.app.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.models.Chapter
import com.cm.app.R
import com.cm.app.activities.ReadActivity
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.History
import com.cm.app.models.Product
import com.cm.app.utils.Constants
import com.google.gson.Gson
import java.util.regex.Pattern

class ChapterAdapter(private var chapterList: ArrayList<Chapter>,private val product:Product,private val historyDao: HistoryDao) :
    RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textChapterName)
        val time: TextView = itemView.findViewById(R.id.textChapterTimeAgo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_chapter_number, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChapter = chapterList[position]
        val gson = Gson()
        val ct = gson.toJson(currentChapter)
        val pr = gson.toJson(this.product)

        holder.name.text = currentChapter.name
        holder.time.text = currentChapter.time

        if (historyDao.getById(this.product.id)?.chapterId == currentChapter.id){
            holder.itemView.setBackgroundResource(R.drawable.border_chapter)
        }

        holder.itemView.setOnClickListener {
            Constants.saveHistory(holder.itemView.context,product,currentChapter)

            val intent = Intent(holder.name.context, ReadActivity::class.java)
            intent.putExtra("chapter", ct)
            intent.putExtra("product", pr)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            val options = ActivityOptions.makeSceneTransitionAnimation(
                holder.name.context as Activity,
                holder.name,
                "transition"
            )

            startActivity(holder.name.context,intent, options.toBundle())
        }
    }

    fun toggleFilter() {
        this.chapterList.reverse()
    }
}