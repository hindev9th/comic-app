package com.cm.app.fragments

import com.cm.app.adapters.ProductAdapter
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.models.Product
import com.cm.app.R
import com.cm.app.repositories.IProductRepository
import com.cm.app.repositories.ProductRepository
import com.cm.app.activities.MainActivity
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.utils.Constants
import org.jsoup.nodes.Document

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<Product>
    private lateinit var iProductRepository: IProductRepository
    private lateinit var productAdapter: ProductAdapter
    private lateinit var process: FrameLayout
    private lateinit var doc : Document
    private var index: Int = 1
    private var isLoading = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        process = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerProducts)
        recyclerView.setHasFixedSize(true)

        iProductRepository = ProductRepository()
        productList = ArrayList()

        val historyDao = HistoryDao(requireContext())
        productAdapter = ProductAdapter(productList, historyDao)
        recyclerView.adapter = productAdapter

        listeners()
        setData()
    }

    private fun listeners() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    index++
                    setData()
                }
            }
        })

    }

    inner class MyNetworkTask : AsyncTask<String, Void, Document>() {
        override fun doInBackground(vararg params: String): Document {
            try {
                doc = Constants.getDataComic(params[0])
            }catch (e :Exception){
                cancel(true)
            }
            return doc
        }
        override fun onPostExecute(result: Document) {
            try {
                loadData()
            }catch (e : Exception){
                process.visibility = View.GONE
                Toast.makeText(this@HomeFragment.context, "Error!", Toast.LENGTH_LONG).show()
            }

        }
        override fun onCancelled() {
            super.onCancelled()
            process.visibility = View.GONE
            Toast.makeText(this@HomeFragment.context, "Can't connect server!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setData() {
        this.isLoading = true
        val task = MyNetworkTask()
        task.execute(Constants.BASE_COMIC_URL +"?page="+this.index)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadData(){
        val  list = iProductRepository.getList(doc)
        productList += list
        productAdapter.notifyDataSetChanged()
        process.visibility = View.GONE
        val handler = Handler()
        handler.postDelayed({
            isLoading = false
        }, 2000)
    }
}