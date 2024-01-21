package com.cm.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.models.ChapterModel

class ChapterAdapter(private var chapterList: ArrayList<ChapterModel>) :
    RecyclerView.Adapter<ChapterAdapter.ViewHolder>() {

    private lateinit var itemClickListener: onItemClickListener

    interface onItemClickListener{
        fun onItemCLick(position: Int)
    }

    fun setOnItemClickListener(itemClickListener: onItemClickListener){
        this.itemClickListener = itemClickListener
    }

    class ViewHolder(itemView: View, itemClickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textChapterName)
        val time: TextView = itemView.findViewById(R.id.textChapterTimeAgo)

        init {
            itemView.setOnClickListener{
                itemClickListener.onItemCLick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_chapter_number, parent, false)
        return ViewHolder(view,itemClickListener)
    }

    override fun getItemCount(): Int {
        return this.chapterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChapter = this.chapterList[position]
        holder.name.text = currentChapter.name
        holder.time.text = currentChapter.time
    }

    fun toggleFilter(){
        this.chapterList.reverse()
    }
}