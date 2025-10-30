package com.vargaj.codelock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vargaj.codelock.ui.theme.CodeLockTheme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeLockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    CheckScreen()
                }
            }
        }
    }
}

@Composable
fun CheckScreen() {
    // --- State variables ---
    var username by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    // --- Layout ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("LeetCode Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button
        Button(
            onClick = { result = "Checking $username..." },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result text
        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello there $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodeLockTheme {
        Greeting("Android")
    }
}