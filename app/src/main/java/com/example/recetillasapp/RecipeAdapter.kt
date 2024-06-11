package com.example.recetillasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onDeleteClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var expandedPosition = -1

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.recipe_name)
        val ingredientsTextView: TextView = view.findViewById(R.id.recipe_ingredients)
        val stepsTextView: TextView = view.findViewById(R.id.recipe_steps)
        val moreOptions: ImageView = view.findViewById(R.id.moreOptions)
        val expandableLayout: LinearLayout = view.findViewById(R.id.expandableLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.nameTextView.text = recipe.name
        holder.ingredientsTextView.text = recipe.ingredients
        holder.stepsTextView.text = recipe.steps

        val isExpandable = position == expandedPosition
        holder.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.nameTextView.setOnClickListener {
            expandedPosition = if (isExpandable) -1 else position
            notifyDataSetChanged()  // Notify the adapter to refresh all items
        }

        holder.moreOptions.setOnClickListener {
            showPopupMenu(holder.moreOptions, recipe)
        }
    }

    override fun getItemCount() = recipes.size

    private fun showPopupMenu(view: View, recipe: Recipe) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.menu_item, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    onDeleteClick(recipe)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
