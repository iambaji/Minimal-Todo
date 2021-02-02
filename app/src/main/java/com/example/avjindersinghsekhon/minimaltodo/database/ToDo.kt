 package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.*
import java.util.*


@Entity(tableName = "todo",indices = [Index(value = ["itemid"],unique = true),])
data class ToDo(
        @PrimaryKey(autoGenerate = true)
        var itemid : Int =0,
                @ColumnInfo(name = "todotitle") var toDoTitle : String,
                @ColumnInfo(name = "tododescription") var toDoDescription : String?,
                @ColumnInfo(name = "hasreminder") var hasReminder : Boolean,
                @ColumnInfo(name = "todoreminddate") var toDoRemindDate: Date? = Date(),
                @ColumnInfo(name = "todocolor") var todoColor: Int?){



    constructor() : this(0,"",null,false,null,null)
}