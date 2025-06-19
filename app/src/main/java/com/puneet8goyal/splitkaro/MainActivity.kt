package com.puneet8goyal.splitkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel // Missing import added
import com.puneet8goyal.splitkaro.ui.screens.ExpenseScreen
import com.puneet8goyal.splitkaro.ui.theme.SplitKaroTheme
import com.puneet8goyal.splitkaro.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitKaroTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    val viewModel: MainViewModel = hiltViewModel() // Now this will work
    var snackBarState by remember { mutableStateOf("") } // Simple state for now

    ExpenseScreen(viewModel = viewModel, snackBarState = snackBarState)
}