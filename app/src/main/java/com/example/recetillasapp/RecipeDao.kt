package com.example.recetillasapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAll(): List<Recipe>

    @Insert
    fun insertAll(vararg recipes: Recipe)

    @Query("DELETE FROM recipe")
    fun deleteAll()

    @Delete
    fun delete(recipe: Recipe)
}
