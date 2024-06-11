package com.example.recetillasapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room

class AddRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val etRecipeName: EditText = findViewById(R.id.etRecipeName)
        val etIngredients: EditText = findViewById(R.id.etIngredients)
        val etSteps: EditText = findViewById(R.id.etSteps)
        val btnSave: Button = findViewById(R.id.btnSave)

        val db = Room.databaseBuilder(
            applicationContext,
            RecipeDatabase::class.java, "recipe-database"
        ).allowMainThreadQueries().build()

        val recipeDao = db.recipeDao()

        btnSave.setOnClickListener {
            val name = etRecipeName.text.toString()
            val ingredients = etIngredients.text.toString()
            val steps = etSteps.text.toString()

            if (name.isNotEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
                val newRecipe = Recipe(name = name, ingredients = ingredients, steps = steps)
                recipeDao.insertAll(newRecipe)
                Log.d("AddRecipeActivity", "Receta guardada: $newRecipe")
                Toast.makeText(this, "Receta guardada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show()
                Log.d("AddRecipeActivity", "Todos los campos deben estar llenos")
            }
        }
    }
}
