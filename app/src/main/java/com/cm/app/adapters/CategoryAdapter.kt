package com.cm.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cm.app.R
import com.cm.app.models.CategoryModel

class CategoryAdapter(private val categoriesList: List<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name:TextView = itemView.findViewById(R.id.textCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_container_category, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.categoriesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCategory = this.categoriesList[position]
        holder.name.text = currentCategory.name
    }
}