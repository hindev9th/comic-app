package com.cm.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.FavoriteAdapter
import com.cm.app.adapters.HistoryAdapter
import com.cm.app.data.database.dao.FavoriteDao
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.Favorite
import com.cm.app.data.database.entities.History

class FavoriteFragment : Fragment() {
    private lateinit var listFavorite : ArrayList<Favorite>
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var recyclerFavorite : RecyclerView
    private lateinit var favoriteDao: FavoriteDao
    private lateinit var textNoComic: TextView
    private var index = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.favoriteDao = FavoriteDao(requireContext())
        this.recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        this.textNoComic = view.findViewById(R.id.textNoComic)

        this.listFavorite = favoriteDao.getFavorite()
        this.favoriteAdapter = FavoriteAdapter(this.listFavorite,favoriteDao)
        this.recyclerFavorite.adapter = this.favoriteAdapter
        if (listFavorite.size > 0 ){
            textNoComic.visibility = View.GONE
        }else{
            textNoComic.visibility = View.VISIBLE
        }
        listens()
//        historyDao.deleteAllHistories()
//        Log.d("AndroidRuntime",historyDao.getHistory(1).toString())
    }

    fun listens(){
        this.favoriteAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (listFavorite.size > 0 ){
                    textNoComic.visibility = View.GONE
                }else{
                    textNoComic.visibility = View.VISIBLE
                }
            }
        })
//        this.recyclerHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!recyclerView.canScrollVertically(1) && !isLoading) {
//                    isLoading = true
//                    index += 1;
//                    listHistory += historyDao.getHistory(index)
//                    historyAdapter.notifyDataSetChanged()
//                    val handler = Handler()
//                    handler.postDelayed({
//                        isLoading = false;
//                    }, 2000)
//                }
//            }
//
//        })
    }
}