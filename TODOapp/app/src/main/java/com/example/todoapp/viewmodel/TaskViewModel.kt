package com.example.todoapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.db.TaskEntity
import com.example.todoapp.repository.DbRepository
import com.example.todoapp.utils.Constants.EDUCATION
import com.example.todoapp.utils.Constants.HEALTH
import com.example.todoapp.utils.Constants.HIGH
import com.example.todoapp.utils.Constants.HOME
import com.example.todoapp.utils.Constants.LOW
import com.example.todoapp.utils.Constants.NORMAL
import com.example.todoapp.utils.Constants.WORK
import com.example.todoapp.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: DbRepository) : ViewModel() {

    val catList = MutableLiveData<MutableList<String>>()
    val prList = MutableLiveData<MutableList<String>>()
    var tasksData = MutableLiveData<DataStatus<List<TaskEntity>>>()
    val taskDetail = MutableLiveData<DataStatus<TaskEntity>>()

    fun loadCatData() = viewModelScope.launch(Dispatchers.IO) {
        val data = mutableListOf(WORK, EDUCATION, HOME, HEALTH)
        catList.postValue(data)
    }

    fun loadPrData() = viewModelScope.launch(Dispatchers.IO) {
        val data = mutableListOf(HIGH, NORMAL, LOW)
        prList.postValue(data)
    }

    fun saveEditTask(isEdite: Boolean, noteEntity: TaskEntity) = viewModelScope.launch(Dispatchers.IO) {
        if (isEdite) {
            repository.updateTask(noteEntity)
        } else {
            repository.saveTask(noteEntity)
        }
    }

    fun getAllNotes() = viewModelScope.launch(Dispatchers.Main) {
        repository.getAllTasks().collect() {
            tasksData.postValue(DataStatus.sucess(it, it.isEmpty()))
        }
    }

    fun deleteAllNotes() = viewModelScope.launch(Dispatchers.Main) {
        repository.deleteAllTasks()
    }

    fun getSearchNotes(search: String) = viewModelScope.launch {
        repository.searchTask(search).collect() {
            tasksData.postValue(DataStatus.sucess(it, it.isEmpty()))
        }
    }

    fun getFilterNotes(filter: String) = viewModelScope.launch {
        repository.filterTask(filter).collect() {
            tasksData.postValue(DataStatus.sucess(it, it.isEmpty()))
        }
    }

    fun deleteNote(entity: TaskEntity) = viewModelScope.launch {
        repository.deleteTask(entity)
    }

    fun getDetailsNote(id: Int) = viewModelScope.launch {
        repository.getDetailsTask(id).collect {
            taskDetail.postValue(DataStatus.sucess(it, false))
        }
    }
}