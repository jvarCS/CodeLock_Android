package com.vargaj.codelock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vargaj.codelock.ui.theme.CodeLockTheme
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient() // Reusable HTTP client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeLockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckScreen(client)
                }
            }
        }
    }
}

@Composable
fun CheckScreen(client: OkHttpClient) {
    // VM var
    val vm: CheckViewModel = viewModel()
    val focusMgr = LocalFocusManager.current

    // This includes the text box and the check button
    Column(
        modifier = Modifier
            .fillMaxSize()  // Fill the entire screen
            .padding(24.dp), // Horizontal padding for the box and button. Higher value means more whitespace between things and edge of screen
        verticalArrangement = Arrangement.Center,   //  Aligns things in the center of the column
        horizontalAlignment = Alignment.CenterHorizontally // Same here
    ) {
        // The stuff above in parenthesis sets parameters for everything contained in the column (text, button)
        // Everything now is in brackets and are the individual things
        OutlinedTextField(
            value = vm.username.value,   // The value entered in the text box will be assigned to the username var
            onValueChange = { vm.username.value = it }, // Updates username whenever the value is changed
            label = { Text("LeetCode Username") },  // Default text
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Spacer to make space between text box and button


        Button(
            onClick = { // When check is clicked
                focusMgr.clearFocus()
                vm.checkUser()
            },
            enabled = !vm.loading.value, // Enabled literally is to set the button (feature in general) as enabled or not. Enabled allows for clicks, not enabled means its greyed out
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check")
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (vm.loading.value) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = vm.result.value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Suspended function for making the API call
suspend fun checkSolvedToday(client: OkHttpClient, username: String): Boolean {
    val url = "https://codelock-0bgc.onrender.com/check?user=$username"
    val request = Request.Builder().url(url).build()


    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
        val json = JSONObject(response.body?.string() ?: "")
        return json.optBoolean("solvedToday", false)
    }
}
