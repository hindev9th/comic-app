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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cm.app.R
import com.cm.app.activities.DetailActivity
import com.cm.app.activities.ReadActivity
import com.cm.app.models.ProductModel

class ProductAdapter(private var mList: ArrayList<ProductModel> = ArrayList()) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var progressBar: FrameLayout = itemView.findViewById(R.id.progressBar)
        val image:ImageView = itemView.findViewById(R.id.ImageProduct)
        val name:TextView = itemView.findViewById(R.id.textName)
        val chapNumber1:TextView = itemView.findViewById(R.id.textChapterNumber1)
        val chapNumber2:TextView = itemView.findViewById(R.id.textChapterNumber2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mList[position]

        Glide.with(holder.image.context).load(currentItem.image).listener(object : RequestListener<Drawable>{
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
        if (currentItem.chap1 == ""){
            holder.chapNumber1.visibility = View.GONE
        }
        if (currentItem.chap2 == ""){
            holder.chapNumber2.visibility = View.GONE
        }
        holder.chapNumber1.text = currentItem.chap1
        holder.chapNumber2.text = currentItem.chap2

        holder.image.setOnClickListener {
            val intent = Intent(holder.image.context,DetailActivity::class.java)
            intent.putExtra("url",currentItem.url)
            intent.putExtra("image",currentItem.image)
            intent.putExtra("name",currentItem.name)
            val activity = holder.image.context as Activity
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.image, "transition_image")

            startActivity(holder.image.context, intent,options.toBundle())
        }
        holder.chapNumber1.setOnClickListener {
            val intent = Intent(holder.chapNumber1.context, ReadActivity::class.java)
            intent.putExtra("url", currentItem.chapUrl1)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val activity = holder.chapNumber1.context as Activity
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.chapNumber1, "image_transition")
            startActivity(holder.image.context,intent,options.toBundle())
        }
        holder.chapNumber2.setOnClickListener {
            val intent = Intent(holder.chapNumber2.context, ReadActivity::class.java)
            intent.putExtra("url", currentItem.chapUrl2)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val activity = holder.chapNumber2.context as Activity
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, holder.chapNumber2, "image_transition")
            startActivity(holder.image.context,intent,options.toBundle())
        }
    }


    override fun getItemCount(): Int {
        return mList.size ?: 0
    }

    fun showToast(context:Context, message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    fun addItems(products: ArrayList<ProductModel>){
        val lastPos: Int = mList.size.minus(1)
        mList.addAll(products)
        notifyItemRangeInserted(lastPos, products.size)
    }
}