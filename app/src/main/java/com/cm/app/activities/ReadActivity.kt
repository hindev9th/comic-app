package com.cm.app.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.PageAdapter
import com.cm.app.models.ChapterModel
import com.cm.app.models.PageModel
import com.cm.app.services.ChapterService
import com.cm.app.services.DetailService
import com.cm.app.utilities.Constants
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class ReadActivity : AppCompatActivity() {
    private lateinit var pageAdapter: PageAdapter
    private lateinit var pageModelList: ArrayList<PageModel>
    private lateinit var chapterModelList: ArrayList<ChapterModel>
    private lateinit var pageListElements: Elements
    private lateinit var chapterListElements: Elements
    private lateinit var recyclerImage : RecyclerView
    private lateinit var progressBar: FrameLayout
    private lateinit var doc : Document
    private lateinit var docOld : Document
    private var isLoading = true
    private var url = ""
    private var currentIndex = 0
    private var typeAdd = "ASC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        this.chapterModelList = arrayListOf<ChapterModel>()
        this.pageModelList = arrayListOf<PageModel>()
        this.recyclerImage = findViewById(R.id.recyclerImages)
        this.progressBar = findViewById(R.id.progressBar)

        this.pageAdapter = PageAdapter(this.pageModelList)
        this.recyclerImage.adapter = this.pageAdapter
        this.url = intent.getStringExtra("url").toString()
        this.docOld = Jsoup.parse(intent.getStringExtra("docOld").toString())
        this.currentIndex = intent.getIntExtra("index",0)

        this.setData()
        this.listenerEvent()
    }

    private fun listenerEvent(){
        this.recyclerImage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()


                    val totalItemCount = layoutManager.itemCount

                    if (lastVisibleItemPosition == totalItemCount - 1 && !isLoading) {
                        try {
                            if (currentIndex > 0 && currentIndex < (chapterModelList.size - 1) ){
                                currentIndex -= 1
                                url = chapterModelList[currentIndex].url
                                typeAdd = "ASC"
                                setData()
                            }
                        }catch (e :Exception){

                        }
                    } else if (layoutManager.findFirstVisibleItemPosition() == 0 && !isLoading) {
                        try {
                            if (currentIndex > 0 && currentIndex < (chapterModelList.size - 1) ){
                                currentIndex += 1
                                url = chapterModelList[currentIndex].url
                                typeAdd = "DESC"
                                setData()
                            }
                        }catch (e :Exception){

                        }
                    }
                }
            }

        })
    }

    fun setData(){
        isLoading = true
        val task = TaskGetImages()
        task.execute(url)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun getData(){
        this.pageListElements = ChapterService.getChapterListImage(doc)
        this.pageListElements.forEach{element ->
            var urlImage = ChapterService.getChapterImageUrl(element)
            val pageModel = PageModel(urlImage)
            if (this.typeAdd == "ASC"){
                this.pageModelList.add(pageModel)
            }else{
                this.pageModelList.add(0,pageModel)
            }
        }

        this.pageAdapter.notifyDataSetChanged()
        this.progressBar.visibility = View.GONE
        val handler = Handler()
        handler.postDelayed({
            this.isLoading = false;
        }, 2000)
    }

    private fun loadChapter(){
        val list = DetailService.getListChapter(this.docOld)
        if (list != null) {
            chapterListElements = list
        }

        chapterListElements.forEach{element ->
            val url = DetailService.getChapterUrl(element)
            val name = DetailService.getChapterName(element)
            val time = DetailService.getChapterTimeAgo(element)
            val chapterModel = ChapterModel(
                url,
                name,
                time
            )
            chapterModelList.add(chapterModel)
        }
    }

    inner class TaskGetImages : AsyncTask<String, Void, Document>() {
        override fun doInBackground(vararg params: String): Document {
            doc = Constants.getDataComic(params[0])
            return doc
        }

        override fun onPostExecute(result: Document?) {
            if (result != null){
                getData()
                loadChapter()
            }
        }
    }
}