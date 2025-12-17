package com.example.hometasker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hometasker.data.local.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY sort_order ASC, name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE parent_id IS NULL ORDER BY sort_order ASC, name ASC")
    fun getRootCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE parent_id = :parentId ORDER BY sort_order ASC, name ASC")
    fun getSubcategories(parentId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoriesCount(): Int
}
