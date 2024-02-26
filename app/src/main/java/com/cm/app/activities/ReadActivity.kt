package com.cm.app.activities

import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cm.app.models.Chapter
import com.cm.app.R
import com.cm.app.models.Product
import com.cm.app.repositories.ChapterRepository
import com.cm.app.utils.Constants
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.gson.Gson
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.regex.Pattern

class ReadActivity : AppCompatActivity() {
    private lateinit var chapterModelList: ArrayList<Chapter>
    private lateinit var progressBar: FrameLayout
    private lateinit var doc: Document
    private lateinit var webView: WebView
    private lateinit var iChapterRepository: ChapterRepository
    private var isLoading = true
    private lateinit var currentName: TextView
    private lateinit var chapterNext: Chapter
    private lateinit var chapterBack: Chapter
    private lateinit var currentChapter :Chapter
    private lateinit var product: Product
    private lateinit var back: ImageView
    private lateinit var next: ImageView
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        this.iChapterRepository = ChapterRepository()
        this.chapterModelList = arrayListOf<Chapter>()
        this.progressBar = findViewById(R.id.progressBar)
        this.next = findViewById(R.id.imageNext)
        this.back = findViewById(R.id.imageBack)
        this.currentName = findViewById(R.id.textCurrentChapterName)

        val gson = Gson()
        this.currentChapter = gson.fromJson(intent.getStringExtra("chapter").toString(),Chapter::class.java)
        this.product = gson.fromJson(intent.getStringExtra("product").toString(),Product::class.java)

        this.webView = findViewById<WebView>(R.id.webView)
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearMatches()
        webView.clearView()

        this.loadPage()
        this.setData()
        listerEvent()
    }

    fun listerEvent() {


        next.setOnClickListener {
            this.currentChapter = this.chapterNext
            Constants.saveHistory(this,this.product,this.currentChapter)
            this.loadPage()
        }
        back.setOnClickListener {
            this.currentChapter = this.chapterBack
            Constants.saveHistory(this,this.product,this.currentChapter)
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
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(this.currentChapter.name)
        matcher.find()

        this.currentName.text = "Chapter ${matcher.group()}"
        this.getPositionCurrentChapter()
        this.progressBar.visibility = View.VISIBLE
        this.webView.settings.javaScriptEnabled = true

        this.webView.webViewClient = object : WebViewClient() {

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.d("AndroidRuntime","run")
                val css =
                    "#header,.notify_block,.top,.reading-control,.mrt5.mrb5.text-center.col-sm-6,.top.bottom,.footer, .reading > .container{display: none !important;;}" //your css as String
                val js =
                    "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js, null)

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
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.GONE

                val css =
                    "#header, .notify_block, .top, .reading-control, .mrt5.mrb5.text-center.col-sm-6, .top.bottom, .footer, .reading > .container {display: none !important;} " //your css as String
                val js =
                    "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js, null)

            }
        }


        this.webView.loadUrl(Constants.BASE_COMIC_URL+this.currentChapter.url)
    }

    fun getPositionCurrentChapter(): Int {
        this.currentIndex = chapterModelList.indexOfFirst { it.id == currentChapter.id }
        return this.currentIndex
    }

    fun setData() {
        isLoading = true
        val taskDetail = TaskGetDetail()
        taskDetail.execute(Constants.BASE_COMIC_URL + this.product.url)
    }

    private fun loadChapter() {
        chapterModelList = this.iChapterRepository.getList(this.doc)
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
        this.getPositionCurrentChapter()
    }


    inner class TaskGetDetail : AsyncTask<String, Void, Document>() {
        override fun doInBackground(vararg params: String): Document {
            doc = Constants.getDataComic(params[0])
            return doc
        }

        override fun onPostExecute(result: Document) {
            loadChapter()
        }
    }
}