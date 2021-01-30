package com.example.avjindersinghsekhon.minimaltodo.viewmodels

import androidx.lifecycle.*
import com.example.avjindersinghsekhon.minimaltodo.Repository
import com.example.avjindersinghsekhon.minimaltodo.database.ToDo
import kotlinx.coroutines.launch

class ListTodoViewModel(private val repository : Repository) : ViewModel() {

    val todoItems : LiveData<List<ToDo>> = repository.todoItems.asLiveData()

    fun insert(item : ToDo) = viewModelScope.launch {
        repository.insert(item)
    }
}

class ListTodoViewModelFactory(private val repository : Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListTodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListTodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}