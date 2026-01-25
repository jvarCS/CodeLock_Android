package com.vargaj.codelock

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckViewModel : ViewModel() {
    private val client = OkHttpClient() // Reusable HTTP client

    var username = mutableStateOf("")
    var loading = mutableStateOf(false)
    var result = mutableStateOf("")

    fun checkUser() {
        val name = username.value.trim()
        if (name.isEmpty()) {
            result.value = "Please enter a username"
            return
        }

        loading.value = true
        result.value = "Checking..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val solved = checkSolvedToday(client,name)
                withContext(Dispatchers.Main) {
                    result.value = if (solved) "✅ Solved today!" else "❌ Not yet solved :("
                }
            } catch (e : Exception) {
                e.printStackTrace()
                result.value = "Error"
            } finally {
                loading.value = false
            }
        }
    }
}
