package com.example.avjindersinghsekhon.minimaltodo.AddToDo

import androidx.lifecycle.*
import com.example.avjindersinghsekhon.minimaltodo.Repository
import com.example.avjindersinghsekhon.minimaltodo.database.ToDo
import kotlinx.coroutines.launch

class AddToDoViewModel(private val repository : Repository) : ViewModel() {

    fun insert(item : ToDo) = viewModelScope.launch {
        repository.insert(item)
    }


    fun getItem(id : Int) : LiveData<ToDo> = repository.getItem(id).asLiveData()
}
class AddToDoViewModelFactory(private val repository : Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}