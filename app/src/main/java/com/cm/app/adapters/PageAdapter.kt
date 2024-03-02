package com.cm.app.adapters

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.cm.app.models.Page
import com.cm.app.R
import com.cm.app.activities.ReadActivity
import com.cm.app.models.Chapter
import com.cm.app.utils.Constants

class PageAdapter(private val pageList: List<Chapter>, private val callbackInterface: CallbackInterface) :
    RecyclerView.Adapter<PageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webView: WebView = itemView.findViewById(R.id.webView)
        val textStart: TextView = itemView.findViewById(R.id.textStart)
        val textEnd: TextView = itemView.findViewById(R.id.textEnd)
        val progressBar: FrameLayout = itemView.findViewById(R.id.progressBar)
    }

    interface CallbackInterface{
        fun setIsLoading(value:Boolean)
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
        if (currentImage.name != ""){
            holder.textStart.text =  "Start chapter ${currentImage.name}"
            holder.textEnd.text =  "End chapter ${currentImage.name}"
            holder.progressBar.visibility = View.VISIBLE
            holder.webView.settings.javaScriptEnabled = true
            holder.webView.webViewClient = object : WebViewClient() {
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    Log.d("AndroidRuntime",url.toString())

                    val css =
                        "#header,.notify_block,.top,.reading-control,#back-to-top,.mrt5.mrb5.text-center.col-sm-6,.top.bottom,.footer, .reading > .container{display: none !important;;}" //your css as String
                    val js =
                        "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                    holder.webView.evaluateJavascript(js, null)
                }
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    val css =
                        "#header, .notify_block, .top, .reading-control,#back-to-top, .mrt5.mrb5.text-center.col-sm-6, .top.bottom, .footer, .reading > .container {display: none !important;} " //your css as String
                    val js =
                        "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                    holder.webView.evaluateJavascript(js, null)
                    holder.progressBar.visibility = View.GONE

                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    callbackInterface.setIsLoading(false)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null || !url.startsWith("https://") || !url.startsWith("http://")) {
                        holder.webView.stopLoading()
                        return false
                    }else{
                        return true
                    }
                }
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
//                    view?.loadUrl("about:blank")
                    Toast.makeText(holder.webView.context, "Error occured, please check newtwork connectivity", Toast.LENGTH_SHORT).show()
                }
            }


            holder.webView.loadUrl(Constants.BASE_COMIC_URL+currentImage.url)
        }else{
            holder.textEnd.text = holder.textEnd.context.getString(R.string.no_new_chapter)
            holder.textStart.visibility = View.GONE
            holder.webView.visibility = View.GONE
        }

    }
}