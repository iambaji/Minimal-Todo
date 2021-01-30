package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*


@Entity(tableName = "todo")
data class ToDo(
                @ColumnInfo(name = "todotitle") var toDoTitle : String,
                @ColumnInfo(name = "tododescription") var toDoDescription : String?,
                @ColumnInfo(name = "hasreminder") var hasReminder : Boolean,
                @ColumnInfo(name = "todoreminddate") var toDoRemindDate: Date? = Date(),
                @ColumnInfo(name = "todocolor") var todoColor: Int?){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemid")
    var itemid : Int = 0

    constructor() : this("",null,false,null,null)
}