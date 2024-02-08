package com.cm.app.activities

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cm.app.R
import com.cm.app.models.ChapterModel
import com.cm.app.services.DetailService
import com.cm.app.utilities.Constants
import com.google.android.material.bottomappbar.BottomAppBar
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class ReadActivity : AppCompatActivity() {
    private lateinit var chapterModelList: ArrayList<ChapterModel>
    private lateinit var chapterListElements: Elements
    private lateinit var progressBar: FrameLayout
    private lateinit var doc: Document
    private lateinit var webView: WebView
    private var isLoading = true
    private lateinit var urlDetail: String
    private lateinit var url: String
    private lateinit var currentName: TextView
    private lateinit var chapterNext: ChapterModel
    private lateinit var chapterBack: ChapterModel
    private var currentIndex = 0
    private lateinit var back: ImageView
    private lateinit var next: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        this.chapterModelList = arrayListOf<ChapterModel>()
        this.progressBar = findViewById(R.id.progressBar)
        this.next = findViewById(R.id.imageNext)
        this.back = findViewById(R.id.imageBack)
        this.currentName = findViewById(R.id.textCurrentChapterName)

        this.url = intent.getStringExtra("url").toString()
        this.urlDetail = intent.getStringExtra("urlDetail").toString()
        this.currentIndex = intent.getIntExtra("index", 0)

        this.webView = findViewById<WebView>(R.id.webView)

        this.loadPage()
        this.setData()
        listerEvent()
    }

    fun listerEvent() {


        next.setOnClickListener {
            this.url = this.chapterNext.url
            this.currentName.text = this.chapterNext.name
            currentIndex -= 1

            this.loadPage()
        }
        back.setOnClickListener {
            this.url = this.chapterBack.url
            this.currentName.text = this.chapterBack.name

            currentIndex += 1
            this.loadPage()

        }

        var bottomBar: BottomAppBar = findViewById(R.id.bottomAppBar)
        webView.setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            val isScrollingUp = scrollY < oldScrollY
            val isScrolledToTop = scrollY == 0

            if (isScrollingUp || isScrolledToTop) {
                // Show the bottom bar
                bottomBar.visibility = View.VISIBLE
            } else {
                // Hide the bottom bar
                bottomBar.visibility = View.INVISIBLE
            }
        }
    }

    fun loadPage() {
        this.progressBar.visibility = View.VISIBLE
        this.webView.settings.javaScriptEnabled = true
        this.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                val css =
                    "#header,.notify_block,.top,.reading-control,.mrt5.mrb5.text-center.col-sm-6,.top.bottom,.footer{display: none;} .reading-detail{width:100%;}" //your css as String
                val js =
                    "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js, null)
                progressBar.visibility = View.GONE

                if ((currentIndex + 1) < chapterModelList.size) {
                    back.visibility = View.VISIBLE
                    chapterBack = chapterModelList[currentIndex + 1]
                } else {
                    back.visibility = View.INVISIBLE
                }

                if ((currentIndex - 1) >= 0) {
                    next.visibility = View.VISIBLE
                    chapterNext = chapterModelList[currentIndex - 1]
                } else {
                    next.visibility = View.INVISIBLE
                }
                super.onPageFinished(view, url)
            }
        }

        this.webView.loadUrl(this.url)
    }

    fun setData() {
        isLoading = true
        val taskDetail = TaskGetDetail()
        taskDetail.execute(urlDetail)
    }

    private fun loadChapter() {
        val list = DetailService.getListChapter(this.doc)
        if (list != null) {
            chapterListElements = list
        }

        chapterListElements.forEachIndexed { index, element ->
            val url = DetailService.getChapterUrl(element)
            val name = DetailService.getChapterName(element)
            val time = DetailService.getChapterTimeAgo(element)
            val chapterModel = ChapterModel(
                url,
                name,
                time
            )
            if (this.url == url) {
                currentIndex = index
                currentName.text = name
            }
            chapterModelList.add(chapterModel)
        }

        if ((currentIndex + 1) < this.chapterModelList.size) {
            this.chapterBack = this.chapterModelList[currentIndex + 1]
        } else {
            val back = findViewById<ImageView>(R.id.imageBack);
            back.visibility = View.INVISIBLE

        }

        if ((currentIndex - 1) >= 0) {
            this.chapterNext = this.chapterModelList[currentIndex - 1]
        } else {
            val next = findViewById<ImageView>(R.id.imageNext);
            next.visibility = View.INVISIBLE
        }

    }


    inner class TaskGetDetail : AsyncTask<String, Void, Document>() {
        override fun doInBackground(vararg params: String): Document {
            doc = Constants.getDataComic(params[0])
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (result != null) {
                loadChapter()
            }
        }
    }
}