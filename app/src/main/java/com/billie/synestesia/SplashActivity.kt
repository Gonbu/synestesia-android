package com.billie.synestesia

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.billie.synestesia.ui.theme.SynestesiaTheme

// Constantes pour le splash screen
private const val SPLASH_DELAY_MS = 2500L
private const val GRADIENT_SIZE_DP = 400
private const val GRADIENT_CENTER_OFFSET = 200f
private const val GRADIENT_RADIUS = 200f
private const val GLOW_BOX_SIZE_DP = 300
private const val GLOW_CENTER_OFFSET = 150f
private const val GLOW_RADIUS = 150f
private const val LOGO_SIZE_DP = 280
private const val LOGO_SCALE = 0.9f
private const val SUBTITLE_FONT_SIZE = 18
private const val SUBTITLE_ALPHA = 0.9f
private const val SPACER_HEIGHT_DP = 24
private const val BACKGROUND_COLOR = 0xFF331B72
private const val GRADIENT_ALPHA = 0.3f
private const val GLOW_ALPHA = 0.1f

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SynestesiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { splashScreen() }
            }
        }

        // Délai de 2.5 secondes avant de lancer MainActivity
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                SPLASH_DELAY_MS
            )
    }
}

@Composable
fun splashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(BACKGROUND_COLOR)),
        contentAlignment = Alignment.Center
    ) {
        // Dégradé radial subtil autour du centre
        radialGradientBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo avec effet de glow
            logoWithGlow()

            Spacer(modifier = Modifier.height(SPACER_HEIGHT_DP.dp))

            // Sous-titre élégant
            subtitleText()
        }
    }
}

@Composable
private fun radialGradientBackground() {
    Box(
        modifier =
        Modifier.size(GRADIENT_SIZE_DP.dp)
            .background(
                brush =
                androidx.compose.ui.graphics.Brush.radialGradient(
                    colors =
                    listOf(
                        Color(BACKGROUND_COLOR)
                            .copy(
                                alpha =
                                GRADIENT_ALPHA
                            ),
                        Color(BACKGROUND_COLOR)
                            .copy(alpha = 0.0f)
                    ),
                    center =
                    androidx.compose.ui.geometry.Offset(
                        GRADIENT_CENTER_OFFSET,
                        GRADIENT_CENTER_OFFSET
                    ),
                    radius = GRADIENT_RADIUS
                )
            )
    )
}

@Composable
private fun logoWithGlow() {
    Box(
        modifier =
        Modifier.size(GLOW_BOX_SIZE_DP.dp)
            .background(
                brush =
                androidx.compose.ui.graphics.Brush.radialGradient(
                    colors =
                    listOf(
                        Color.White.copy(
                            alpha = GLOW_ALPHA
                        ),
                        Color.Transparent
                    ),
                    center =
                    androidx.compose.ui.geometry.Offset(
                        GLOW_CENTER_OFFSET,
                        GLOW_CENTER_OFFSET
                    ),
                    radius = GLOW_RADIUS
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "Synestesia Logo",
            modifier = Modifier.size(LOGO_SIZE_DP.dp).scale(LOGO_SCALE)
        )
    }
}

@Composable
private fun subtitleText() {
    Text(
        text = "Vos souvenirs, votre histoire",
        style =
        MaterialTheme.typography.bodyLarge.copy(
            fontSize = SUBTITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Light
        ),
        color = Color.White.copy(alpha = SUBTITLE_ALPHA)
    )
}
