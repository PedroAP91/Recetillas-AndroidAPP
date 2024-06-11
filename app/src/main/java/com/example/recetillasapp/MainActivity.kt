package com.example.recetillasapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = RecipeDatabase.getDatabase(applicationContext)
        val recipeDao = db.recipeDao()

        // Observa cambios en la lista de recetas y actualiza el RecyclerView
        recipeViewModel.recipes.observe(this, Observer { recipes ->
            Log.d("MainActivity", "Recetas observadas: $recipes")
            recyclerView.adapter = RecipeAdapter(recipes) { recipe ->
                showDeleteConfirmationDialog(recipeDao, recipe)
            }
        })

        // Configura el ícono del menú
        val menuIcon: ImageView = findViewById(R.id.menuIcon)
        menuIcon.setOnClickListener { showPopupMenu(it, recipeDao) }
    }

    override fun onResume() {
        super.onResume()
        val db = RecipeDatabase.getDatabase(applicationContext)
        val recipeDao = db.recipeDao()
        lifecycleScope.launch {
            val allRecipes = withContext(Dispatchers.IO) { recipeDao.getAll() }
            recipeViewModel.setRecipes(allRecipes)
        }
    }

    private fun showPopupMenu(view: View, recipeDao: RecipeDao) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_main, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_add_recipe -> {
                    val intent = Intent(this@MainActivity, AddRecipeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_delete_recipes -> {
                    showDeleteAllConfirmationDialog(recipeDao)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDeleteAllConfirmationDialog(recipeDao: RecipeDao) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmar Borrado")
            setMessage("¿Estás seguro de que deseas borrar todas las recetas?")
            setPositiveButton("Sí") { dialog, which ->
                // Borrar todas las recetas
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        recipeDao.deleteAll()
                    }
                    withContext(Dispatchers.Main) {
                        recipeViewModel.setRecipes(emptyList())
                    }
                }
            }
            setNegativeButton("No", null)
        }.show()
    }

    private fun showDeleteConfirmationDialog(recipeDao: RecipeDao, recipe: Recipe) {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmar Borrado")
            setMessage("¿Estás seguro de que deseas borrar esta receta?")
            setPositiveButton("Sí") { dialog, which ->
                // Borrar la receta específica
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        recipeDao.delete(recipe)
                        val updatedRecipes = recipeDao.getAll()
                        withContext(Dispatchers.Main) {
                            recipeViewModel.setRecipes(updatedRecipes)
                        }
                    }
                }
            }
            setNegativeButton("No", null)
        }.show()
    }
}
