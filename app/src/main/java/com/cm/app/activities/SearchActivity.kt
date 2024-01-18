package com.cm.app.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.ProductAdapter
import com.cm.app.models.ProductModel
import com.cm.app.services.ProductService
import com.cm.app.utilities.Constants
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<ProductModel>
    private lateinit var dataList: Elements
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar : ProgressBar
    private lateinit var doc : Document
    private lateinit var search : ImageView
    private var index: Int = 1
    private var isLoading = true;
    private var url = Constants.BASE_COMIC_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        this.progressBar = findViewById(R.id.progressBar)

        this.recyclerView = findViewById(R.id.recyclerProducts)
        this.recyclerView.setHasFixedSize(true)

        this.productList = arrayListOf<ProductModel>()

        this.productAdapter = ProductAdapter(this.productList)
        this.recyclerView.adapter = this.productAdapter

        this.listeners()
    }

    private fun listeners() {
        val home = findViewById<ImageView>(R.id.imageHome)
        search = findViewById<ImageView>(R.id.imageSearchAction)
        val inputSearch = findViewById<EditText>(R.id.inputSearch)
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
            val intent = Intent(this@SearchActivity,MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        inputSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var inputString:String = inputSearch.text.toString()
                var stringNew = inputString.trim()
                stringNew = stringNew.replace(" ", "+")
                this.url = this.url + "tim-truyen?keyword=" + stringNew
                inputSearch.clearFocus()
                hideKeyboard(v)
                this.isLoading = true
                this.index = 1
                this.progressBar.visibility = View.VISIBLE
                search.visibility = View.INVISIBLE
                setData()
                return@setOnEditorActionListener true // Consume the event
            }
            false
        }

        search.setOnClickListener{
            var inputString:String = inputSearch.text.toString()
            var stringNew = inputString.trim()
            stringNew = stringNew.replace(" ", "+")
            this.url = this.url + "tim-truyen?keyword=" + stringNew
            inputSearch.clearFocus()
            hideKeyboard(it)
            this.isLoading = true
            this.index = 1
            this.progressBar.visibility = View.VISIBLE
            search.visibility = View.INVISIBLE
            setData()
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
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setData() {

        this.isLoading = true
        val task = MyNetworkTask()
        task.execute(this.url+"&page=" + this.index)
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
        search.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            this.isLoading = false;
        }, 2000)

    }
}