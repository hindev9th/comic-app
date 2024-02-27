package com.cm.app.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.ui.unit.Constraints
import androidx.fragment.app.Fragment
import com.cm.app.R
import com.cm.app.utils.Constants

class SettingFragment : Fragment() {
    private lateinit var switchNextChapter : SwitchCompat
    private var sharedPref: SharedPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        switchNextChapter = view.findViewById(R.id.switchNextChapter)

        listerEvents()
        loadData()
    }

    private fun listerEvents() {
        switchNextChapter.setOnClickListener{
            Constants.setDefaults(Constants.SCROLL_NEXT_CHAPTER,switchNextChapter.isChecked,requireContext())
        }
    }

    fun loadData(){
        val isChecked = Constants.getBoolean(Constants.SCROLL_NEXT_CHAPTER,requireContext())
        switchNextChapter.isChecked = isChecked
    }
}