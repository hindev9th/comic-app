package com.cm.app.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.models.Product
import com.cm.app.R
import com.cm.app.repositories.IProductRepository
import com.cm.app.repositories.ProductRepository
import com.cm.app.adapters.ProductAdapter
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.utils.Constants
import org.jsoup.nodes.Document

class SearchFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: ArrayList<Product>
    private lateinit var iProductRepository: IProductRepository
    private lateinit var productAdapter: ProductAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var doc: Document
    private lateinit var search: ImageView
    private lateinit var inputSearch: EditText
    private var index: Int = 1
    private var isLoading = true;
    private var url = Constants.BASE_COMIC_URL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        this.recyclerView = view.findViewById(R.id.recyclerProducts)
        search = view.findViewById<ImageView>(R.id.imageSearchAction)
        inputSearch = view.findViewById<EditText>(R.id.inputSearch)


        this.recyclerView.setHasFixedSize(true)

        this.iProductRepository = ProductRepository()
        this.productList = arrayListOf<Product>()

        val historyDao = HistoryDao(requireContext())
        this.productAdapter = ProductAdapter(this.productList,historyDao)
        this.recyclerView.adapter = this.productAdapter

        this.listeners()
    }

    @SuppressLint("ResourceType")
    private fun listeners() {
        search.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.primary),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        this.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    index += 1;
                    setData()
                }
            }

        })

        inputSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var inputString: String = inputSearch.text.toString()
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
                return@setOnEditorActionListener true
            }
            false
        }

        search.setOnClickListener {
            var inputString: String = inputSearch.text.toString()
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

    inner class MyNetworkTask : AsyncTask<String, Void, Document>() {

        override fun doInBackground(vararg params: String): Document {
            try {
                doc = Constants.getDataComic(params[0])
            } catch (e: Exception) {
                cancel(true);
            }
            return doc
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onPostExecute(document: Document) {
            try {
                productList += iProductRepository.getList(document)
                productAdapter.notifyDataSetChanged()

                progressBar.visibility = View.GONE
                search.visibility = View.VISIBLE
                val handler = Handler()
                handler.postDelayed({
                    isLoading = false;
                }, 2000)
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
            }
        }

        override fun onCancelled() {
            super.onCancelled()
            progressBar.visibility = View.GONE
            search.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Can't connect server!", Toast.LENGTH_LONG).show()
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setData() {

        this.isLoading = true
        val task = MyNetworkTask()
        task.execute(this.url + "&page=" + this.index)
    }

}