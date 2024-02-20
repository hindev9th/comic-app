package com.cm.app.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.adapters.HistoryAdapter
import com.cm.app.data.database.dao.HistoryDao
import com.cm.app.data.database.entities.History

class HistoryFragment : Fragment() {
    private lateinit var listHistory : ArrayList<History>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerHistory : RecyclerView
    private lateinit var historyDao : HistoryDao
    private var index = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.historyDao = HistoryDao(requireContext())
        this.recyclerHistory = view.findViewById(R.id.recyclerHistory)

        this.listHistory = historyDao.getHistory(1)
        this.historyAdapter = HistoryAdapter(this.listHistory,historyDao)
        this.recyclerHistory.adapter = this.historyAdapter
        listens()
//        historyDao.deleteAllHistories()
//        Log.d("AndroidRuntime",historyDao.getHistory(1).toString())
    }

    fun listens(){
        this.recyclerHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    isLoading = true
                    index += 1;
                    listHistory += historyDao.getHistory(index)
                    historyAdapter.notifyDataSetChanged()
                    val handler = Handler()
                    handler.postDelayed({
                        isLoading = false;
                    }, 2000)
                }
            }

        })
    }
}