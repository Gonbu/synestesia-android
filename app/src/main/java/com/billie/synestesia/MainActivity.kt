package com.billie.synestesia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.billie.synestesia.ui.theme.SynestesiaTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Authentification anonyme Firebase
        if (FirebaseAuth.getInstance().currentUser == null) {
            FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // Gérer l’erreur de connexion si besoin
                    }
                }
        }
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
