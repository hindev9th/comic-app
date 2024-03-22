package com.cm.app.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.models.Chapter
import com.cm.app.utils.Constants

class PageAdapter(
    private val pageList: List<Chapter>,
    private val callbackInterface: CallbackInterface
) : RecyclerView.Adapter<PageAdapter.ViewHolder>() {

    interface CallbackInterface {
        fun setIsLoading(value: Boolean)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webView: WebView = itemView.findViewById(R.id.webView)
        val textStart: TextView = itemView.findViewById(R.id.textStart)
        val textEnd: TextView = itemView.findViewById(R.id.textEnd)
        val progressBar: FrameLayout = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = pageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = pageList[position]

        if (currentImage.name.isNotEmpty()) {
            holder.textStart.text = holder.itemView.context.getString(R.string.start_chapter, currentImage.name)
            holder.textEnd.text = holder.itemView.context.getString(R.string.end_chapter, currentImage.name)
            holder.progressBar.visibility = View.VISIBLE

            holder.webView.clearCache(true)
            holder.webView.clearHistory()
            holder.webView.clearMatches()
            holder.webView.clearView()
            holder.webView.clearFormData()

            holder.webView.apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        val css =
                            "#header, .notify_block, .top, .reading-control,#back-to-top, .mrt5.mrb5.text-center.col-sm-6, .top.bottom, .footer, .reading > .container {display: none !important;} " //your css as String
                        val js =
                            "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                        view?.evaluateJavascript(js, null)
                    }
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        val css =
                            "#header, .notify_block, .top, .reading-control,#back-to-top, .mrt5.mrb5.text-center.col-sm-6, .top.bottom, .footer, .reading > .container {display: none !important;} " //your css as String
                        val js =
                            "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                        view?.evaluateJavascript(js, null)
                        holder.progressBar.visibility = View.GONE
                    }
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        callbackInterface.setIsLoading(false)
                    }

                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return if ((url == null) || !url.startsWith("https://") || !url.startsWith("http://")) {
                            view?.stopLoading()
                            false
                        }else{
                            true
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                    }
                }
                loadUrl(Constants.BASE_COMIC_URL + currentImage.url)
            }
        } else {
            holder.textStart.visibility = View.GONE
            holder.webView.visibility = View.GONE
            holder.textEnd.setText(R.string.no_new_chapter)
        }
    }
}
