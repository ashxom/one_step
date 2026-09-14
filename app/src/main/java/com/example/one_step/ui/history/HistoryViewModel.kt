package com.example.one_step.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.one_step.domain.model.GuideRecord
import com.example.one_step.domain.repository.GuideLocalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val _records = MutableStateFlow<List<GuideRecord>>(emptyList())
    val records: StateFlow<List<GuideRecord>> = _records.asStateFlow()
    private var repository: GuideLocalRepository? = null
    private var observeJob: Job? = null

    fun attachRepository(localRepository: GuideLocalRepository) {
        if (repository === localRepository) return
        repository = localRepository
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            localRepository.observeRecords().collect { _records.value = it }
        }
    }
}
