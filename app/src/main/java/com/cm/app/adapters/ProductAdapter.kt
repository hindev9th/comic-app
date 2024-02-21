package com.cm.app.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cm.app.models.Product
import com.cm.app.R
import com.cm.app.activities.DetailActivity
import com.cm.app.activities.ReadActivity
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.History
import com.cm.app.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class ProductAdapter(
    private var mList: ArrayList<Product> = ArrayList(),
    private val historyDao: HistoryDao
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: FrameLayout = itemView.findViewById(R.id.progressBar)
        val image: ImageView = itemView.findViewById(R.id.ImageProduct)
        val name: TextView = itemView.findViewById(R.id.textName)
        val chapNumber1: TextView = itemView.findViewById(R.id.textChapterNumber1)
        val chapNumber2: TextView = itemView.findViewById(R.id.textChapterNumber2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mList[position]

        val gson = Gson()
        val product = gson.toJson(currentItem)
        val chapter1 = gson.toJson(currentItem.chapFirst)
        val chapter2 = gson.toJson(currentItem.chapSecond)

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
        holder.name.text = currentItem.name
        if (currentItem.chapFirst.name == "") {
            holder.chapNumber1.visibility = View.GONE
        }
        if (currentItem.chapSecond.name == "") {
            holder.chapNumber2.visibility = View.GONE
        }
        holder.chapNumber1.text = currentItem.chapFirst.name
        holder.chapNumber2.text = currentItem.chapSecond.name

        holder.image.setOnClickListener {
            val intent = Intent(holder.image.context, DetailActivity::class.java)
            intent.putExtra("product", product)
            val activity = holder.image.context as Activity
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                holder.image,
                "transition_image"
            )

            startActivity(holder.image.context, intent, options.toBundle())
        }
        holder.chapNumber1.setOnClickListener {
            Constants.saveHistory(holder.itemView.context,currentItem,currentItem.chapFirst)

            val intent = Intent(holder.chapNumber1.context, ReadActivity::class.java)
            intent.putExtra("chapter", chapter1)
            intent.putExtra("product", product)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val activity = holder.chapNumber1.context as Activity
            val options =
                ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "transition")
            startActivity(holder.image.context, intent, options.toBundle())
        }
        holder.chapNumber2.setOnClickListener {
            Constants.saveHistory(holder.itemView.context,currentItem,currentItem.chapSecond)

            val intent = Intent(holder.chapNumber2.context, ReadActivity::class.java)
            intent.putExtra("chapter", chapter2)
            intent.putExtra("product", product)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val activity = holder.chapNumber2.context as Activity
            val options =
                ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "transition")
            startActivity(holder.image.context, intent, options.toBundle())
        }
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}