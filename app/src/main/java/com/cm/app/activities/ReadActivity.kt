package com.cm.app.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.PageAdapter
import com.cm.app.models.Chapter
import com.cm.app.models.Product
import com.cm.app.repositories.ChapterRepository
import com.cm.app.utils.Constants
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.gson.Gson
import org.jsoup.nodes.Document
import java.util.regex.Pattern

class ReadActivity : AppCompatActivity(), PageAdapter.CallbackInterface {
    private lateinit var chapterModelList: ArrayList<Chapter>
    private lateinit var pageModelList: ArrayList<Chapter>
    private lateinit var pageAdapter: PageAdapter
    private lateinit var recyclerPage: RecyclerView
    private lateinit var progressBar: FrameLayout
    private lateinit var progressBarNext: ProgressBar
    private lateinit var progressBarBack: ProgressBar
    private lateinit var doc: Document
    private lateinit var webView: WebView
    private lateinit var iChapterRepository: ChapterRepository
    private var isLoading = true
    private var isShowEnd = false
    private lateinit var currentName: TextView
    private lateinit var chapterNext: Chapter
    private lateinit var chapterBack: Chapter
    private lateinit var currentChapter :Chapter
    private lateinit var product: Product
    private lateinit var back: ImageView
    private lateinit var next: ImageView
    private lateinit var bottomAppBar: BottomAppBar
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        this.iChapterRepository = ChapterRepository()
        this.chapterModelList = arrayListOf()
        this.progressBar = findViewById(R.id.progressBar)
        this.progressBarNext = findViewById(R.id.progressBarNextChapter)
        this.progressBarBack = findViewById(R.id.progressBarBackChapter)
        this.next = findViewById(R.id.imageNext)
        this.back = findViewById(R.id.imageBack)
        this.currentName = findViewById(R.id.textCurrentChapterName)
        this.recyclerPage = findViewById(R.id.recyclerImages)
        this.bottomAppBar = findViewById(R.id.bottomAppBar)
        pageModelList = arrayListOf()
        val isChecked = Constants.getBoolean(Constants.SCROLL_NEXT_CHAPTER,this)

        val gson = Gson()
        this.currentChapter = gson.fromJson(intent.getStringExtra("chapter").toString(),Chapter::class.java)
        this.product = gson.fromJson(intent.getStringExtra("product").toString(),Product::class.java)



        this.webView = findViewById(R.id.webView)
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearMatches()
        webView.clearView()

        if (isChecked){
            pageModelList.add(currentChapter)
            pageAdapter = PageAdapter(pageModelList,this)
            recyclerPage.adapter = pageAdapter
            scrollEvent()
            this.webView.visibility = View.GONE
            this.bottomAppBar.visibility = View.GONE
            this.recyclerPage.visibility = View.VISIBLE
        }else{
            this.webView.visibility = View.VISIBLE
            this.bottomAppBar.visibility = View.VISIBLE
            this.recyclerPage.visibility = View.GONE
            this.loadPage()
        }
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

    fun scrollEvent(){
        this.recyclerPage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading){
//                    if (dy < 0){
//                        if (!recyclerView.canScrollVertically(-1) && dy != 0 && (currentIndex + 1) < chapterModelList.size){
//                            Log.d("AndroidRuntime","Back")
//                            isLoading = true
//                            currentChapter = chapterModelList[currentIndex + 1]
//                            pageModelList.add(0,chapterModelList[currentIndex + 1])
////                            Constants.saveHistory(baseContext,product,currentChapter)
//                            pageAdapter.notifyItemInserted(0)
//                            recyclerView.smoothScrollToPosition(0)
//                            getPositionCurrentChapter()
//                        }
//                    }
                    if (dy > 0) { //check for scroll down
                        if(!recyclerView.canScrollVertically(1) && dy != 0)
                        {
                            if ((currentIndex - 1) >= 0){
                                Log.d("AndroidRuntime","NEXT")
                                isLoading = true
                                currentChapter = chapterModelList[currentIndex - 1]
                                pageModelList.add(chapterModelList[currentIndex - 1])
                                Constants.saveHistory(baseContext,product,currentChapter)
                                recyclerPage.adapter?.notifyItemInserted(pageModelList.size - 1)
                                getPositionCurrentChapter()
                            }else{
                                if (!isShowEnd){
                                    val chapter = Chapter("","","","","")
                                    pageModelList.add(chapter)
                                    recyclerPage.adapter?.notifyItemInserted(pageModelList.size - 1)
//                                    pageAdapter.notifyItemInserted(pageModelList.size - 1)
                                    isShowEnd = true
                                }
                            }


                        }
                    }
                }

            }
        })
    }

    fun loadPage() {
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(this.currentChapter.name)
        matcher.find()
        hideNext()
        this.currentName.text = "Chapter ${matcher.group()}"
        this.getPositionCurrentChapter()
        this.webView.settings.javaScriptEnabled = true
        progressBar.visibility = View.VISIBLE
        this.webView.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                val css =
                    "#header,.notify_block,.top,.reading-control,#back-to-top,.mrt5.mrb5.text-center.col-sm-6,.top.bottom,.footer, .reading > .container{display: none !important;;}" //your css as String
                val js =
                    "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"

                webView.evaluateJavascript(js, null)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

                val css =
                    "#header, .notify_block, .top, .reading-control,#back-to-top, .mrt5.mrb5.text-center.col-sm-6, .top.bottom, .footer, .reading > .container {display: none !important;} " //your css as String
                val js =
                    "var style = document.createElement('style'); style.innerHTML = '$css'; document.head.appendChild(style);"
                webView.evaluateJavascript(js, null)
                progressBar.visibility = View.GONE

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showNext()
                loadNext()
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null || !url.startsWith("https://") || !url.startsWith("http://")) {
                    view?.stopLoading()
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
//                view?.loadUrl("about:blank")
                Toast.makeText(this@ReadActivity, "Error occured, please check newtwork connectivity", Toast.LENGTH_SHORT).show()
            }
        }


        this.webView.loadUrl(Constants.BASE_COMIC_URL+this.currentChapter.url)
    }

    fun getPositionCurrentChapter(): Int {
        this.currentIndex = chapterModelList.indexOfFirst { it.chapterId == currentChapter.chapterId }
        return this.currentIndex
    }

    fun loadNext(){
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

    fun showNext(){
        progressBarNext.visibility = View.GONE
        progressBarBack.visibility = View.GONE
        next.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
    }

    fun hideNext(){
        progressBarNext.visibility = View.VISIBLE
        progressBarBack.visibility = View.VISIBLE
        next.visibility = View.GONE
        back.visibility = View.GONE
    }

    override fun setIsLoading(value: Boolean) {
        val handler = Handler()
        handler.postDelayed({
            isLoading = value
        }, 5000)
    }
}