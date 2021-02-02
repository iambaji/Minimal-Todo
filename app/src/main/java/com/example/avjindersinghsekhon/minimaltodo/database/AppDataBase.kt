package com.example.avjindersinghsekhon.minimaltodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [ToDo::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class AppDataBase : RoomDatabase() {
    abstract fun getTodoItemDao() : ToDoItemDao
    companion object{

        private var INSTANCE : AppDataBase? = null

        fun getDataBase(context : Context, scope: CoroutineScope) : AppDataBase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(context.applicationContext,
                AppDataBase::class.java,
                "todoapp_database").
                addCallback(DataBaseCallback(scope)).fallbackToDestructiveMigration().
                build()
            }
        }
    }


    private class DataBaseCallback(private val scope : CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    val todoDao = it.getTodoItemDao()
                    todoDao.deleteAll()
                    println("database first creation call back")
                    val item1 = ToDo( 0,"This is title","this is description",
                    false,null,null)
                    val item2 = ToDo(0,"This is title","this is description",
                            false,null,null)
                    val item3 = ToDo(0,"This is 3","this is description",
                            false,null,null)
                    val item4 = ToDo(0,"This is 4","this is description",
                            false,null,null)
                    todoDao.insertItem(item1)
                    todoDao.insertItem(item2)
                    todoDao.insertItem(item3)
                    todoDao.insertItem(item4)

                }
            }
        }
    }
}



