package com.cm.app.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cm.app.R
import com.cm.app.activities.DetailActivity
import com.cm.app.activities.ReadActivity
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.History
import com.cm.app.models.Chapter
import com.cm.app.models.Product
import com.cm.app.utils.Constants
import com.google.gson.Gson

class HistoryAdapter(
    private var mList: ArrayList<History> = ArrayList(),
    private val historyDao: HistoryDao
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: FrameLayout = itemView.findViewById(R.id.progressBarImage)
        val image: ImageView = itemView.findViewById(R.id.imageProduct)
        val name: TextView = itemView.findViewById(R.id.textName)
        val chapterName: TextView = itemView.findViewById(R.id.textCurrentChapter)
        val chapterNameNew: TextView = itemView.findViewById(R.id.textChapterNew)
        val chapterTitleNameNew: TextView = itemView.findViewById(R.id.textTitleChapterNew)
        val btnDelete: TextView = itemView.findViewById(R.id.textDelete)
        val btnRead: TextView = itemView.findViewById(R.id.textRead)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_product, parent, false)
        return HistoryAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolder, position: Int) {
        val currentItem = mList[position]

        val gson = Gson()
        val product = gson.toJson(currentItem)
        val chapter = gson.toJson(
            Chapter(
                currentItem.chapterId,
                currentItem.chapterUrl,
                currentItem.chapterName,
                "",
                ""
            )
        )

        holder.name.text = currentItem.name
        holder.chapterName.text = currentItem.chapterName

        holder.btnDelete.setOnClickListener { e ->
            historyDao.deleteHistoryById(currentItem.id)
            mList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.chapterTitleNameNew.visibility = View.GONE
        holder.btnRead.setOnClickListener { e ->
            val intent = Intent(holder.name.context, ReadActivity::class.java)
            intent.putExtra("product", product)
            intent.putExtra("chapter", chapter)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                holder.name.context as Activity,
                holder.name,
                "transition"
            )

            ContextCompat.startActivity(holder.name.context, intent, options.toBundle())
        }

        holder.image.setOnClickListener { e ->
            val intent = Intent(holder.image.context, DetailActivity::class.java)
            intent.putExtra("product", product)
            val activity = holder.image.context as Activity
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                holder.image,
                "transition_image"
            )

            ContextCompat.startActivity(holder.image.context, intent, options.toBundle())
        }

        Glide.with(holder.image.context).load(Constants.getBaseImageUrl() + currentItem.urlImage)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.progressBar.visibility = View.GONE
                    return false
                }
            }).into(holder.image)
    }
}