package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TelegramViewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TelegramApp()
                }
            }
        }
    }
}

@Composable
fun TelegramApp(
    viewModel: TelegramViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val selectedChatId by viewModel.selectedChatId.collectAsStateWithLifecycle()

    if (!userProfile.isRegistered) {
        AuthScreen(viewModel = viewModel)
    } else if (selectedChatId != null) {
        BackHandler {
            viewModel.selectChat(null)
        }
        ChatScreen(
            viewModel = viewModel,
            onBack = { viewModel.selectChat(null) }
        )
    } else {
        ChatListScreen(
            viewModel = viewModel,
            onChatClick = { chatId ->
                viewModel.selectChat(chatId)
            }
        )
    }
}
