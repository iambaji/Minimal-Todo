package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoItemDao {
    @Query("SELECT * FROM todo")
    fun getAllItems() : Flow<List<ToDo>>

    @Query ("SELECT * FROM todo WHERE itemid =:id")
    fun getItem(id: Int) : Flow<ToDo>

    @Delete
    suspend fun deleteItem(item : ToDo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item : ToDo) : Long

    @Update
    suspend fun updateItem(item: ToDo) : Int

    @Query("DELETE FROM todo ")
    suspend fun deleteAll()

}