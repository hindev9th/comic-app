package com.cm.app.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.cm.app.R
import com.cm.app.models.PageModel

class PageAdapter(private val pageList: List<PageModel>) :
    RecyclerView.Adapter<PageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imagePage)
        val progressBar: FrameLayout = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.pageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = this.pageList[position]
        Glide.with(holder.image.context).load(currentImage.image)
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
            })
            .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    holder.image.setImageDrawable(resource)

                    // Get the intrinsic width and height of the loaded image
                    val intrinsicWidth = resource.intrinsicWidth
                    val intrinsicHeight = resource.intrinsicHeight


                    // Set the height of the ImageView to match its intrinsic height
                    holder.image.layoutParams.height = intrinsicHeight
                    holder.image.requestLayout()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Implementation if needed when the image is cleared
                }
            })
    }
}