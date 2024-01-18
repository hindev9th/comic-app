package com.cm.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.ProductAdapter
import com.cm.app.models.ProductModel
import com.cm.app.services.ProductService
import com.cm.app.utilities.Constants
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<ProductModel>
    private lateinit var dataList: Elements
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar : FrameLayout
    private lateinit var doc : Document
    private var index: Int = 1
    private var isLoading = true;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.progressBar = findViewById(R.id.progressBar)

        this.recyclerView = findViewById(R.id.recyclerProducts)
        this.recyclerView.setHasFixedSize(true)

        this.productList = arrayListOf<ProductModel>()
        this.productAdapter = ProductAdapter(this.productList)
        this.recyclerView.adapter = this.productAdapter
        this.setData()
        this.listeners()

    }

    private fun listeners() {
        val home = findViewById<ImageView>(R.id.imageHome)
        val search = findViewById<ImageView>(R.id.imageSearch)
        this.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    index += 1;
                    setData()
                }
            }

        })

        home.setOnClickListener{
            this.isLoading = true
            this.progressBar.visibility = View.VISIBLE
            val task = MyNetworkTask()
            task.execute(Constants.BASE_COMIC_URL)
        }

        search.setOnClickListener{
            val intent = Intent(this@MainActivity,SearchActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    inner class MyNetworkTask : AsyncTask<String, Void, Elements>() {

        override fun doInBackground(vararg params: String): Elements? {
            doc = Constants.getDataComic(params[0])
            return ProductService.getListComic(doc)
        }

        override fun onPostExecute(result: Elements?) {
            if (result != null) {
                dataList = result
                getData()
            } else {
                // Handle error
            }
        }
    }

    private fun setData() {

        this.isLoading = true
        val task = MyNetworkTask()
        task.execute(Constants.BASE_COMIC_URL+"?page="+this.index)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        this.dataList.forEach { element ->
            val url = ProductService.getComicUrl(element)
            val name = ProductService.getComicName(element)
            val image = ProductService.getImageComicUrl(element)
            var chap1 = ""
            var chapkey1 = ""
            var chap2 = ""
            var chapkey2 = ""
            try {
                chap1 = ProductService.getComicChapter1(element)
                chapkey1 = ProductService.getComicChapter1Url(element)
                chap2 = ProductService.getComicChapter2(element)
                chapkey2 = ProductService.getComicChapter2Url(element)
            }catch (e : Exception){

            }

            val productModel = ProductModel(
                url,
                image,
                name,
                chapkey1,
                chap1,
                chapkey2,
                chap2
            )

            this.productList.add(productModel)
        }

        this.productAdapter.notifyDataSetChanged()

        this.progressBar.visibility = View.GONE
        val handler = Handler()
        handler.postDelayed({
            this.isLoading = false;
        }, 2000)

    }
}