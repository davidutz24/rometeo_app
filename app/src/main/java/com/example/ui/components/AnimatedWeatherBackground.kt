package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.model.WeatherConditionType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimatedWeatherBackground(
    conditionType: WeatherConditionType,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_anim")

    // General rotation or translation tick
    val animTick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "anim_tick"
    )

    // Lightning pulse for thunderstorm
    val lightningFlash by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightning_tick"
    )

    // Twinkling stars seed
    val stars = remember {
        List(35) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 0.6f,
                size = Random.nextFloat() * 2.5f + 1f,
                speed = Random.nextFloat() * 2f + 1f
            )
        }
    }

    // Rain particles seed
    val rainDrops = remember {
        List(40) {
            RainDrop(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                length = Random.nextFloat() * 25f + 15f,
                speed = Random.nextFloat() * 0.7f + 0.6f
            )
        }
    }

    // Snowflakes seed
    val snowFlakes = remember {
        List(30) {
            SnowFlake(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 3.5f + 1.5f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                phase = Random.nextFloat() * 2 * PI.toFloat()
            )
        }
    }

    // Background gradient colors
    val bgColors = when (conditionType) {
        WeatherConditionType.CLEAR_DAY, WeatherConditionType.MAINLY_CLEAR_DAY -> if (isDark) {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0369A1))
        } else {
            listOf(Color(0xFF38BDF8), Color(0xFF7DD3FC), Color(0xFFBAE6FD), Color(0xFFF0F9FF))
        }
        WeatherConditionType.CLEAR_NIGHT, WeatherConditionType.MAINLY_CLEAR_NIGHT -> listOf(
            Color(0xFF060B19), Color(0xFF0D1B2A), Color(0xFF1B263B)
        )
        WeatherConditionType.PARTLY_CLOUDY_DAY -> if (isDark) {
            listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF1E3A5F))
        } else {
            listOf(Color(0xFF60A5FA), Color(0xFF93C5FD), Color(0xFFDBEAFE))
        }
        WeatherConditionType.PARTLY_CLOUDY_NIGHT -> listOf(
            Color(0xFF060B19), Color(0xFF0F172A), Color(0xFF1E293B)
        )
        WeatherConditionType.OVERCAST -> if (isDark) {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
        } else {
            listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1), Color(0xFFE2E8F0))
        }
        WeatherConditionType.FOG -> if (isDark) {
            listOf(Color(0xFF111827), Color(0xFF1F2937), Color(0xFF374151))
        } else {
            listOf(Color(0xFFCBD5E1), Color(0xFFE2E8F0), Color(0xFFF1F5F9))
        }
        WeatherConditionType.DRIZZLE, WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN -> if (isDark) {
            listOf(Color(0xFF090E17), Color(0xFF0F1E36), Color(0xFF142B4D))
        } else {
            listOf(Color(0xFF475569), Color(0xFF64748B), Color(0xFF94A3B8))
        }
        WeatherConditionType.SNOW -> if (isDark) {
            listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF283548))
        } else {
            listOf(Color(0xFF93C5FD), Color(0xFFBFDBFE), Color(0xFFE0F2FE))
        }
        WeatherConditionType.THUNDERSTORM -> {
            val flashAlpha = if (lightningFlash > 0.88f) 0.35f else 0f
            val baseColor = if (isDark) Color(0xFF090B14) else Color(0xFF1E293B)
            val flashColor = Color(0xFF38BDF8).copy(alpha = flashAlpha)
            listOf(baseColor, Color(0xFF151928), flashColor)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Draw atmospheric background gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = bgColors,
                startY = 0f,
                endY = height
            )
        )

        // 2. Draw condition-specific elements
        when (conditionType) {
            WeatherConditionType.CLEAR_DAY, WeatherConditionType.MAINLY_CLEAR_DAY -> {
                drawSunRays(width = width, animTick = animTick, isDark = isDark)
            }
            WeatherConditionType.CLEAR_NIGHT, WeatherConditionType.MAINLY_CLEAR_NIGHT -> {
                drawStarryNight(stars = stars, animTick = animTick, width = width, height = height)
            }
            WeatherConditionType.PARTLY_CLOUDY_DAY, WeatherConditionType.PARTLY_CLOUDY_NIGHT, WeatherConditionType.OVERCAST -> {
                if (conditionType == WeatherConditionType.PARTLY_CLOUDY_DAY) {
                    drawSunRays(width = width, animTick = animTick, isDark = isDark, scale = 0.6f)
                }
                drawFloatingClouds(animTick = animTick, width = width, height = height, isDark = isDark)
            }
            WeatherConditionType.DRIZZLE, WeatherConditionType.RAIN, WeatherConditionType.HEAVY_RAIN -> {
                drawFloatingClouds(animTick = animTick, width = width, height = height, isDark = isDark, alpha = 0.4f)
                drawFallingRain(rainDrops = rainDrops, animTick = animTick, width = width, height = height)
            }
            WeatherConditionType.SNOW -> {
                drawFallingSnow(snowFlakes = snowFlakes, animTick = animTick, width = width, height = height)
            }
            WeatherConditionType.THUNDERSTORM -> {
                drawFloatingClouds(animTick = animTick, width = width, height = height, isDark = isDark, alpha = 0.5f)
                drawFallingRain(rainDrops = rainDrops, animTick = animTick, width = width, height = height, speedMultiplier = 1.4f)
                if (lightningFlash > 0.92f) {
                    drawLightningBolt(width = width, height = height)
                }
            }
            WeatherConditionType.FOG -> {
                drawMistLayers(animTick = animTick, width = width, height = height, isDark = isDark)
            }
        }
    }
}

private fun DrawScope.drawSunRays(width: Float, animTick: Float, isDark: Boolean, scale: Float = 1f) {
    val sunCenterX = width * 0.85f
    val sunCenterY = 140f
    val baseRadius = 55f * scale
    val glowColor = if (isDark) Color(0xFFF59E0B).copy(alpha = 0.25f) else Color(0xFFFDE047).copy(alpha = 0.35f)
    val rayColor = if (isDark) Color(0xFFFBBF24).copy(alpha = 0.15f) else Color(0xFFFFFFFF).copy(alpha = 0.25f)

    // Glow halo
    drawCircle(
        color = glowColor,
        radius = baseRadius * 2.2f,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Sun disc
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFFBEB), Color(0xFFF59E0B)),
            center = Offset(sunCenterX, sunCenterY),
            radius = baseRadius
        ),
        radius = baseRadius,
        center = Offset(sunCenterX, sunCenterY)
    )

    // Rotating rays
    val rayCount = 8
    val angleOffset = animTick * 360f
    for (i in 0 until rayCount) {
        val angleRad = (i * (360f / rayCount) + angleOffset) * (PI / 180f)
        val startDist = baseRadius * 1.25f
        val endDist = baseRadius * 1.8f
        val x1 = sunCenterX + (cos(angleRad) * startDist).toFloat()
        val y1 = sunCenterY + (sin(angleRad) * startDist).toFloat()
        val x2 = sunCenterX + (cos(angleRad) * endDist).toFloat()
        val y2 = sunCenterY + (sin(angleRad) * endDist).toFloat()
        drawLine(
            color = rayColor,
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = 3f * scale
        )
    }
}

private fun DrawScope.drawStarryNight(stars: List<Star>, animTick: Float, width: Float, height: Float) {
    // Crescent Moon
    val moonX = width * 0.82f
    val moonY = 130f
    val moonR = 40f
    drawCircle(
        color = Color(0xFFE2E8F0),
        radius = moonR,
        center = Offset(moonX, moonY)
    )
    drawCircle(
        color = Color(0xFF0D1B2A),
        radius = moonR * 0.85f,
        center = Offset(moonX - 12f, moonY - 10f)
    )

    // Stars
    stars.forEach { s ->
        val alpha = 0.3f + 0.7f * ((sin((animTick * s.speed * 2 * PI).toFloat()) + 1f) / 2f)
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = s.size,
            center = Offset(s.x * width, s.y * height)
        )
    }
}

private fun DrawScope.drawFloatingClouds(animTick: Float, width: Float, height: Float, isDark: Boolean, alpha: Float = 0.25f) {
    val cloudColor = if (isDark) Color(0xFF64748B).copy(alpha = alpha) else Color(0xFFFFFFFF).copy(alpha = alpha)

    // Cloud Layer 1
    val offset1 = ((animTick * 1.2f) % 1f) * (width + 300f) - 150f
    drawCloudShape(x = offset1, y = 100f, scale = 1.2f, color = cloudColor)

    // Cloud Layer 2
    val offset2 = (((animTick * 0.7f) + 0.5f) % 1f) * (width + 300f) - 150f
    drawCloudShape(x = offset2, y = 180f, scale = 0.9f, color = cloudColor.copy(alpha = alpha * 0.7f))
}

private fun DrawScope.drawCloudShape(x: Float, y: Float, scale: Float, color: Color) {
    val r = 40f * scale
    drawCircle(color = color, radius = r, center = Offset(x, y))
    drawCircle(color = color, radius = r * 1.3f, center = Offset(x + r * 1.1f, y - r * 0.3f))
    drawCircle(color = color, radius = r * 0.9f, center = Offset(x + r * 2.1f, y + r * 0.1f))
    drawRoundRect(
        color = color,
        topLeft = Offset(x - r * 0.5f, y),
        size = androidx.compose.ui.geometry.Size(r * 3.2f, r * 0.9f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.5f)
    )
}

private fun DrawScope.drawFallingRain(
    rainDrops: List<RainDrop>,
    animTick: Float,
    width: Float,
    height: Float,
    speedMultiplier: Float = 1f
) {
    val rainColor = Color(0xFF38BDF8).copy(alpha = 0.55f)
    val slant = 10f

    rainDrops.forEach { drop ->
        val progress = (drop.y + animTick * drop.speed * 4f * speedMultiplier) % 1f
        val currentY = progress * height
        val currentX = (drop.x * width + progress * slant) % width

        drawLine(
            color = rainColor,
            start = Offset(currentX, currentY),
            end = Offset(currentX + slant * 0.3f, currentY + drop.length),
            strokeWidth = 2.2f
        )
    }
}

private fun DrawScope.drawFallingSnow(
    snowFlakes: List<SnowFlake>,
    animTick: Float,
    width: Float,
    height: Float
) {
    val snowColor = Color.White.copy(alpha = 0.8f)

    snowFlakes.forEach { flake ->
        val progress = (flake.y + animTick * flake.speed * 1.5f) % 1f
        val curY = progress * height
        val horizontalWave = sin(progress * 4 * PI + flake.phase).toFloat() * 15f
        val curX = (flake.x * width + horizontalWave) % width

        drawCircle(
            color = snowColor,
            radius = flake.radius,
            center = Offset(curX, curY)
        )
    }
}

private fun DrawScope.drawLightningBolt(width: Float, height: Float) {
    val boltPath = Path().apply {
        val startX = width * 0.5f
        moveTo(startX, 60f)
        lineTo(startX - 25f, 130f)
        lineTo(startX + 10f, 135f)
        lineTo(startX - 20f, 220f)
    }
    drawPath(
        path = boltPath,
        color = Color(0xFFE0F2FE),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
    )
}

private fun DrawScope.drawMistLayers(animTick: Float, width: Float, height: Float, isDark: Boolean) {
    val mistColor = if (isDark) Color(0xFF475569).copy(alpha = 0.2f) else Color(0xFFE2E8F0).copy(alpha = 0.35f)
    val y1 = 120f
    val waveOffset = sin(animTick * 2 * PI).toFloat() * 20f
    drawRoundRect(
        color = mistColor,
        topLeft = Offset(-50f + waveOffset, y1),
        size = androidx.compose.ui.geometry.Size(width + 100f, 40f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f)
    )
    drawRoundRect(
        color = mistColor,
        topLeft = Offset(-20f - waveOffset, y1 + 50f),
        size = androidx.compose.ui.geometry.Size(width + 80f, 35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(17f)
    )
}

private data class Star(val x: Float, val y: Float, val size: Float, val speed: Float)
private data class RainDrop(val x: Float, val y: Float, val length: Float, val speed: Float)
private data class SnowFlake(val x: Float, val y: Float, val radius: Float, val speed: Float, val phase: Float)
