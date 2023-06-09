package com.example.todoapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.utils.Constants.TASK_TABLE


@Entity(tableName = TASK_TABLE)
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title : String = "",
    var desc : String = "",
    var cat : String = "",
    var pr : String = "",
)
