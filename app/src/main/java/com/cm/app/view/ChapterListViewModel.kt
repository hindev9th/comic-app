package com.cm.app.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cm.app.models.Chapter
import com.cm.app.repositories.ChapterRepository
import kotlinx.coroutines.launch

class ChapterListViewModel(private val  repository: ChapterRepository) : ViewModel() {
    private val _chapters = MutableLiveData<List<Chapter>>()
    val chapters: LiveData<List<Chapter>> = _chapters
    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean> = _loadingState

    fun loadChapters(comicId : String){
        viewModelScope.launch {
            val chapters = repository.getChapters(comicId)
            _chapters.value = chapters
            _loadingState.value = false
        }
    }
}