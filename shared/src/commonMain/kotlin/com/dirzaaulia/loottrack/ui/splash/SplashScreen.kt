package com.dirzaaulia.loottrack.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dirzaaulia.loottrack.theme.CyanAccent
import com.dirzaaulia.loottrack.theme.ElectricPurpleSecondary
import com.dirzaaulia.loottrack.theme.NeonPinkPrimary
import kotlinx.coroutines.delay
import loottrack.shared.generated.resources.Res
import loottrack.shared.generated.resources.logo_vector
import org.jetbrains.compose.resources.painterResource

private val SplashBackgroundColor = Color(0xFF050508)

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashFinished: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1800L)
        isVisible = false
        delay(350L) // Delay for fadeOut animation
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition()

    // Rubik's Cube Snappy Solving Rotation Animation (Matching Android AnimatedVectorDrawable)
    val cubeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0f at 0 using FastOutSlowInEasing
                90f at 200 using FastOutSlowInEasing
                90f at 250
                180f at 450 using FastOutSlowInEasing
                180f at 500
                270f at 700 using FastOutSlowInEasing
                270f at 750
                360f at 950 using FastOutSlowInEasing
                360f at 1000
            },
            repeatMode = RepeatMode.Restart
        )
    )

    // Perspective 3D Squeeze on Face Twists
    val cubeScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1.00f at 0
                0.87f at 100
                1.00f at 200
                1.00f at 250
                0.87f at 350
                1.00f at 450
                1.00f at 500
                0.87f at 600
                1.00f at 700
                1.00f at 750
                0.87f at 850
                1.00f at 950
                1.00f at 1000
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val logoAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(350))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SplashBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                // Prominent Animated Vector Logo
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle Glow Aura behind the Vector Logo
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(cubeScale * 1.05f)
                            .alpha(glowAlpha * 0.4f)
                            .border(width = 2.dp, color = NeonPinkPrimary, shape = RectangleShape)
                    )

                    // The Actual Animated Vector Logo with Rubik's Cube Twist Animation
                    Image(
                        painter = painterResource(Res.drawable.logo_vector),
                        contentDescription = "LootTrack Vector Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = cubeRotation
                                scaleX = cubeScale
                                scaleY = cubeScale
                            }
                            .alpha(logoAlpha)
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // App Title with Abstract Editorial Typography
                Text(
                    text = "LOOTTRACK",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle Abstract Chip
                Box(
                    modifier = Modifier
                        .background(Color(0xFF17122B), shape = RectangleShape)
                        .border(1.dp, ElectricPurpleSecondary, RectangleShape)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "GAME DEALS ENGINE",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Abstract Segmented Loading Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(logoAlpha)
                            .background(NeonPinkPrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(glowAlpha)
                            .background(ElectricPurpleSecondary)
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(logoAlpha)
                            .background(CyanAccent)
                    )
                }
            }
        }
    }
}
