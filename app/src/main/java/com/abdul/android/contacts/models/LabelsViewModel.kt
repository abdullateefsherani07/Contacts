package com.abdul.android.contacts.models

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.android.contacts.repositories.LabelsRepository
import kotlinx.coroutines.launch

class LabelsViewModel(application: Application): AndroidViewModel(application) {

    private val _labelsState = mutableStateOf(LabelsState())
    val labelsState: State<LabelsState> = _labelsState

    private val repository = LabelsRepository(application.contentResolver)
    var labels by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        loadLabels()
    }

    private fun loadLabels(){
        viewModelScope.launch {
            try {
                labels = repository.getLabels()
                _labelsState.value = _labelsState.value.copy(
                    loading = false,
                    labelsList = labels,
                    error = null
                )
            } catch(e: Exception) {
                _labelsState.value = _labelsState.value.copy(
                    loading = false,
                    error = "Error fetching labels ${e.message}"
                )
            }
        }
    }

    fun fetchLabels(){
        loadLabels()
    }

    data class LabelsState(
        var loading: Boolean = true,
        var labelsList: List<String> = emptyList(),
        var error: String? = null
    )

}