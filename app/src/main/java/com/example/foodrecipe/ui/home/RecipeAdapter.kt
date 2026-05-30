package com.example.foodrecipe.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.foodrecipe.R
import com.example.foodrecipe.data.model.RecipeSummary

class RecipeAdapter(
    private val onClick: (RecipeSummary) -> Unit
) : ListAdapter<RecipeSummary, RecipeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)

        fun bind(recipe: RecipeSummary) {
            tvTitle.text = recipe.title
            ivImage.load(recipe.image)
            itemView.setOnClickListener { onClick(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<RecipeSummary>() {
        override fun areItemsTheSame(old: RecipeSummary, new: RecipeSummary) = old.id == new.id
        override fun areContentsTheSame(old: RecipeSummary, new: RecipeSummary) = old == new
    }
}
