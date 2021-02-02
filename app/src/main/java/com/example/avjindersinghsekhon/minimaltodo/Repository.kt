package com.example.avjindersinghsekhon.minimaltodo

import androidx.annotation.WorkerThread
import com.example.avjindersinghsekhon.minimaltodo.database.ToDo
import com.example.avjindersinghsekhon.minimaltodo.database.ToDoItemDao
import kotlinx.coroutines.flow.Flow

class Repository(private val toDoItemDao: ToDoItemDao) {
    val todoItems : Flow<List<ToDo>> = toDoItemDao.getAllItems()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item : ToDo) : Long{
        return toDoItemDao.insertItem(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(item : ToDo) : Int{
        return toDoItemDao.updateItem(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
     fun getItem(itemid : Int) : Flow<ToDo>{
        return toDoItemDao.getItem(itemid)
    }
}