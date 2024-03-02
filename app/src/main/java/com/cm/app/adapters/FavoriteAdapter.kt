package com.cm.app.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cm.app.R
import com.cm.app.ViewModel.ChapterListViewModel
import com.cm.app.activities.DetailActivity
import com.cm.app.activities.ReadActivity
import com.cm.app.data.database.dao.FavoriteDao
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.Favorite
import com.cm.app.models.Chapter
import com.cm.app.models.Product
import com.cm.app.network.ChapterApi
import com.cm.app.repositories.ChapterRepository
import com.cm.app.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FavoriteAdapter(
    private var mList: ArrayList<Favorite> = ArrayList(),
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao,
    private val viewModel: ChapterListViewModel,
    private val repository: ChapterRepository
) :
    RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: FrameLayout = itemView.findViewById(R.id.progressBarImage)
        val image: ImageView = itemView.findViewById(R.id.imageProduct)
        val name: TextView = itemView.findViewById(R.id.textName)
        val chapterName: TextView = itemView.findViewById(R.id.textCurrentChapter)
        val chapterNameNew: TextView = itemView.findViewById(R.id.textChapterNew)
        val btnDelete: TextView = itemView.findViewById(R.id.textDelete)
        val btnRead: TextView = itemView.findViewById(R.id.textRead)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_product, parent, false)
        return FavoriteAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolder, position: Int) {
        val currentItem = mList[position]
        viewModel.viewModelScope.launch {
            holder.chapterNameNew.text = repository.getChapters(currentItem.id)?.get(0)?.name
        }
        val gson = Gson()
        val product = gson.toJson(currentItem)
        var chapter = gson.toJson(
            Chapter(
                currentItem.chapterId,
                currentItem.chapterUrl,
                currentItem.chapterName,
                "",
                ""
            )
        )

        val history = historyDao.getById(currentItem.id)
        if (history != null) {
            chapter = gson.toJson(
                Chapter(
                    history.chapterId,
                    history.chapterUrl,
                    history.chapterName,
                    "",
                    ""
                )
            )
        }

        holder.name.text = currentItem.name

        holder.chapterName.text = currentItem.chapterName
        if (history != null) {
            holder.chapterName.text = history.chapterName
        }

        holder.btnDelete.setOnClickListener { e ->
            favoriteDao.deleteFavoriteById(currentItem.id)
            mList.removeAt(position)
            notifyDataSetChanged()
        }

        holder.btnRead.setOnClickListener { e ->
            val intent = Intent(holder.name.context, ReadActivity::class.java)
            intent.putExtra("chapter", chapter)
            intent.putExtra("product", product)
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