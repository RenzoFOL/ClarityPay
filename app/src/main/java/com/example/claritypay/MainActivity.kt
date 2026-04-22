package com.example.claritypay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.presentation.navigation.ClarityPayAppRoot
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.ui.theme.ClarityPayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClarityPayTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val app = application as ClarityPayApp
                    ClarityPayAppRoot(
                        sessionViewModel = viewModel(
                            factory = AppViewModelFactory(app.container)
                        )
                    )
                }
            }
        }
    }
}
