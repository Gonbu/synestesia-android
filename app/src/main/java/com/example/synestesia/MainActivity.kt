package com.example.synestesia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.synestesia.ui.theme.SynestesiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            SynestesiaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SouvenirMap(
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}
