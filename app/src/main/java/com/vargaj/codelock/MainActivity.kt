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
import com.vargaj.codelock.ui.theme.CodeLockTheme
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
    //
    var username by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

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
            value = username,   // The value entered in the text box will be assigned to the username var
            onValueChange = { username = it }, // Updates username whenever the value is changed
            label = { Text("LeetCode Username") },  // Default text
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Spacer to make space between text box and button

        Button(
            onClick = { // When check is clicked
                if (username.isNotEmpty()) {
                    result = "Checking..."  // Set the result var to this
                    // Launch background coroutine
                    CoroutineScope(Dispatchers.IO).launch {//
                        try {
                            val solved = checkSolvedToday(client, username)
                            result = if (solved) "✅ Solved today!" else "❌ Not yet solved."
                        } catch (e: Exception) {
                            result = "Error: ${e.message}"
                        }
                    }
                } else {
                    result = "Enter a username first!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = result,
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
