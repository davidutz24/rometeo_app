package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.WeatherCondition
import com.example.model.WeatherConditionType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Premium meteorological vector icon renderer.
 * Accurately represents weather states with multi-layer color blending,
 * sun rays, distinct "Mainly Clear" sun-dominant design, glowing moons,
 * animated rain ripples, thunderstorms with lightning, and soft snow crystals.
 */
@Composable
fun WeatherIcon(
    condition: WeatherCondition,
    size: Dp = 32.dp,
    tint: Color? = null,
    animate: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_icon_anim")
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )
    val cloudFloat by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_float"
    )
    val rainDropShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rain_drop_shift"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val scaleAnim = if (animate) sunPulse else 1.0f
            val floatAnim = if (animate) cloudFloat else 0f
            val rainAnim = if (animate) rainDropShift else 0.5f

            when (condition.type) {
                WeatherConditionType.CLEAR_DAY -> {
                    drawVibrantSun(w, h, scaleAnim, tint)
                }
                WeatherConditionType.CLEAR_NIGHT -> {
                    drawCrescentMoon(w, h, scaleAnim, tint)
                }
                WeatherConditionType.MAINLY_CLEAR_DAY -> {
                    // Predominantly sun with a subtle, miniature puff cloud on the bottom-right
                    drawMainlyClearDay(w, h, scaleAnim, floatAnim, tint)
                }
                WeatherConditionType.MAINLY_CLEAR_NIGHT -> {
                    drawMainlyClearNight(w, h, scaleAnim, floatAnim, tint)
                }
                WeatherConditionType.PARTLY_CLOUDY_DAY -> {
                    drawPartlyCloudyDay(w, h, scaleAnim, floatAnim, tint)
                }
                WeatherConditionType.PARTLY_CLOUDY_NIGHT -> {
                    drawPartlyCloudyNight(w, h, scaleAnim, floatAnim, tint)
                }
                WeatherConditionType.OVERCAST -> {
                    drawOvercastClouds(w, h, floatAnim, tint)
                }
                WeatherConditionType.FOG -> {
                    drawFogLayers(w, h, tint)
                }
                WeatherConditionType.DRIZZLE -> {
                    drawRainyCloud(w, h, floatAnim, rainAnim, isHeavy = false, isDrizzle = true, tint = tint)
                }
                WeatherConditionType.RAIN -> {
                    drawRainyCloud(w, h, floatAnim, rainAnim, isHeavy = false, isDrizzle = false, tint = tint)
                }
                WeatherConditionType.HEAVY_RAIN -> {
                    drawRainyCloud(w, h, floatAnim, rainAnim, isHeavy = true, isDrizzle = false, tint = tint)
                }
                WeatherConditionType.SNOW -> {
                    drawSnowCloud(w, h, floatAnim, tint)
                }
                WeatherConditionType.THUNDERSTORM -> {
                    drawThunderstorm(w, h, floatAnim, rainAnim, tint)
                }
            }
        }
    }
}

// --- Specific Vector Drawing Implementations ---

private fun DrawScope.drawVibrantSun(w: Float, h: Float, pulse: Float, customTint: Color?) {
    val center = Offset(w * 0.5f, h * 0.5f)
    val sunRadius = (w * 0.24f) * pulse

    val coreColor = customTint ?: Color(0xFFF59E0B) // Rich Amber Sun
    val rayColor = customTint?.copy(alpha = 0.85f) ?: Color(0xFFFBBF24)
    val glowColor = customTint?.copy(alpha = 0.25f) ?: Color(0xFFFEF08A).copy(alpha = 0.35f)

    // Soft outer sun halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor, Color.Transparent),
            center = center,
            radius = w * 0.48f
        ),
        radius = w * 0.48f,
        center = center
    )

    // Sun Rays (8 radiant rays)
    val rayLen = w * 0.12f
    val rayDist = sunRadius + w * 0.06f
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f).toFloat()
        val startX = center.x + cos(angle) * rayDist
        val startY = center.y + sin(angle) * rayDist
        val endX = center.x + cos(angle) * (rayDist + rayLen)
        val endY = center.y + sin(angle) * (rayDist + rayLen)

        drawLine(
            color = rayColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = w * 0.07f,
            cap = StrokeCap.Round
        )
    }

    // Solid Sun disk
    drawCircle(
        color = coreColor,
        radius = sunRadius,
        center = center
    )
}

private fun DrawScope.drawCrescentMoon(w: Float, h: Float, pulse: Float, customTint: Color?) {
    val center = Offset(w * 0.5f, h * 0.5f)
    val moonColor = customTint ?: Color(0xFF38BDF8)
    val glowColor = (customTint ?: Color(0xFF38BDF8)).copy(alpha = 0.2f)

    // Outer moon halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(glowColor, Color.Transparent),
            center = center,
            radius = w * 0.45f
        ),
        radius = w * 0.45f,
        center = center
    )

    val moonPath = Path().apply {
        val r = w * 0.30f * pulse
        // Full arc
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center.x - r,
                center.y - r,
                center.x + r,
                center.y + r
            ),
            startAngleDegrees = -110f,
            sweepAngleDegrees = 220f,
            forceMoveTo = false
        )
        // Inner cutout arc to form sharp crescent
        cubicTo(
            center.x - r * 0.1f, center.y + r * 0.7f,
            center.x - r * 0.1f, center.y - r * 0.7f,
            center.x + cos((-110f * Math.PI / 180f).toFloat()) * r,
            center.y + sin((-110f * Math.PI / 180f).toFloat()) * r
        )
        close()
    }

    drawPath(path = moonPath, color = moonColor)

    // Add 2 subtle little stars
    drawCircle(color = moonColor.copy(alpha = 0.8f), radius = w * 0.035f, center = Offset(w * 0.76f, h * 0.28f))
    drawCircle(color = moonColor.copy(alpha = 0.6f), radius = w * 0.025f, center = Offset(w * 0.84f, h * 0.52f))
}

/**
 * MAINLY CLEAR DAY:
 * The sun dominates the frame (taking up 75% of visual weight)
 * with a small, clean cloud puff tucked neatly in the bottom right corner.
 */
private fun DrawScope.drawMainlyClearDay(w: Float, h: Float, pulse: Float, floatAnim: Float, customTint: Color?) {
    // 1. Dominant Radiant Sun centered-left
    val sunCenter = Offset(w * 0.42f, h * 0.40f)
    val sunRadius = (w * 0.22f) * pulse
    val sunColor = customTint ?: Color(0xFFF59E0B)
    val rayColor = customTint?.copy(alpha = 0.85f) ?: Color(0xFFFBBF24)

    // Ray bursts around the exposed parts of sun
    val rayLen = w * 0.10f
    val rayDist = sunRadius + w * 0.05f
    for (i in 0 until 8) {
        val angle = (i * 45f) * (Math.PI / 180f).toFloat()
        // Skip rays that would collide directly into the small bottom cloud
        if (i in 2..3) continue
        val startX = sunCenter.x + cos(angle) * rayDist
        val startY = sunCenter.y + sin(angle) * rayDist
        val endX = sunCenter.x + cos(angle) * (rayDist + rayLen)
        val endY = sunCenter.y + sin(angle) * (rayDist + rayLen)

        drawLine(
            color = rayColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = w * 0.06f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(
        color = sunColor,
        radius = sunRadius,
        center = sunCenter
    )

    // 2. Small, subtle cloud puff in bottom-right corner
    val cloudOffset = Offset(w * 0.64f, h * 0.68f + floatAnim)
    drawCompactCloudPuff(cloudOffset, scale = w * 0.26f, cloudColor = Color(0xFFE2E8F0).copy(alpha = 0.95f))
}

private fun DrawScope.drawMainlyClearNight(w: Float, h: Float, pulse: Float, floatAnim: Float, customTint: Color?) {
    val moonCenter = Offset(w * 0.44f, h * 0.42f)
    val moonColor = customTint ?: Color(0xFF38BDF8)

    val moonPath = Path().apply {
        val r = w * 0.24f * pulse
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                moonCenter.x - r,
                moonCenter.y - r,
                moonCenter.x + r,
                moonCenter.y + r
            ),
            startAngleDegrees = -110f,
            sweepAngleDegrees = 220f,
            forceMoveTo = false
        )
        cubicTo(
            moonCenter.x - r * 0.1f, moonCenter.y + r * 0.7f,
            moonCenter.x - r * 0.1f, moonCenter.y - r * 0.7f,
            moonCenter.x + cos((-110f * Math.PI / 180f).toFloat()) * r,
            moonCenter.y + sin((-110f * Math.PI / 180f).toFloat()) * r
        )
        close()
    }
    drawPath(path = moonPath, color = moonColor)

    // Small cloud puff bottom right
    val cloudOffset = Offset(w * 0.64f, h * 0.68f + floatAnim)
    drawCompactCloudPuff(cloudOffset, scale = w * 0.25f, cloudColor = Color(0xFF94A3B8).copy(alpha = 0.85f))
}

private fun DrawScope.drawPartlyCloudyDay(w: Float, h: Float, pulse: Float, floatAnim: Float, customTint: Color?) {
    // Sun in top-right
    val sunCenter = Offset(w * 0.62f, h * 0.36f)
    val sunRadius = (w * 0.18f) * pulse
    val sunColor = Color(0xFFF59E0B)

    // 4 visible top-right sun rays
    val rayLen = w * 0.08f
    val rayDist = sunRadius + w * 0.04f
    listOf(-45f, 0f, 45f, -90f).forEach { deg ->
        val angle = (deg * Math.PI / 180f).toFloat()
        drawLine(
            color = Color(0xFFFBBF24),
            start = Offset(sunCenter.x + cos(angle) * rayDist, sunCenter.y + sin(angle) * rayDist),
            end = Offset(sunCenter.x + cos(angle) * (rayDist + rayLen), sunCenter.y + sin(angle) * (rayDist + rayLen)),
            strokeWidth = w * 0.055f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(
        color = sunColor,
        radius = sunRadius,
        center = sunCenter
    )

    // Medium Cloud in front (center-bottom)
    val cloudColor = customTint ?: Color(0xFFCBD5E1)
    drawStandardCloudShape(
        centerX = w * 0.44f,
        centerY = h * 0.62f + floatAnim,
        width = w * 0.68f,
        height = h * 0.44f,
        color = cloudColor
    )
}

private fun DrawScope.drawPartlyCloudyNight(w: Float, h: Float, pulse: Float, floatAnim: Float, customTint: Color?) {
    val moonCenter = Offset(w * 0.64f, h * 0.35f)
    val moonColor = Color(0xFF38BDF8)

    val moonPath = Path().apply {
        val r = w * 0.18f * pulse
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                moonCenter.x - r,
                moonCenter.y - r,
                moonCenter.x + r,
                moonCenter.y + r
            ),
            startAngleDegrees = -120f,
            sweepAngleDegrees = 220f,
            forceMoveTo = false
        )
        close()
    }
    drawPath(path = moonPath, color = moonColor)

    // Cloud in front
    val cloudColor = customTint ?: Color(0xFF94A3B8)
    drawStandardCloudShape(
        centerX = w * 0.44f,
        centerY = h * 0.62f + floatAnim,
        width = w * 0.68f,
        height = h * 0.44f,
        color = cloudColor
    )
}

private fun DrawScope.drawOvercastClouds(w: Float, h: Float, floatAnim: Float, customTint: Color?) {
    val backCloudColor = Color(0xFF64748B)
    val frontCloudColor = customTint ?: Color(0xFF94A3B8)

    // Back darker cloud
    drawStandardCloudShape(
        centerX = w * 0.58f,
        centerY = h * 0.42f - floatAnim,
        width = w * 0.70f,
        height = h * 0.44f,
        color = backCloudColor
    )

    // Front cloud
    drawStandardCloudShape(
        centerX = w * 0.42f,
        centerY = h * 0.58f + floatAnim,
        width = w * 0.72f,
        height = h * 0.46f,
        color = frontCloudColor
    )
}

private fun DrawScope.drawFogLayers(w: Float, h: Float, customTint: Color?) {
    val fogColor = customTint ?: Color(0xFF94A3B8)
    val strokeW = w * 0.08f

    // 4 horizontal rounded misty bars
    val bars = listOf(
        Pair(0.26f, 0.65f),
        Pair(0.42f, 0.85f),
        Pair(0.58f, 0.72f),
        Pair(0.74f, 0.55f)
    )

    bars.forEach { (yFrac, lenFrac) ->
        val y = h * yFrac
        val barLen = w * lenFrac
        val startX = (w - barLen) / 2f
        val endX = startX + barLen

        drawLine(
            color = fogColor.copy(alpha = 0.88f),
            start = Offset(startX, y),
            end = Offset(endX, y),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawRainyCloud(
    w: Float,
    h: Float,
    floatAnim: Float,
    rainAnim: Float,
    isHeavy: Boolean,
    isDrizzle: Boolean,
    tint: Color?
) {
    val cloudColor = Color(0xFF64748B)
    val rainColor = tint ?: Color(0xFF38BDF8)

    // Top cloud
    drawStandardCloudShape(
        centerX = w * 0.50f,
        centerY = h * 0.42f + floatAnim,
        width = w * 0.74f,
        height = h * 0.46f,
        color = cloudColor
    )

    // Rain drop streaks falling
    val dropCount = if (isHeavy) 4 else if (isDrizzle) 2 else 3
    val dropLen = if (isHeavy) h * 0.18f else if (isDrizzle) h * 0.08f else h * 0.13f
    val strokeWidth = if (isHeavy) w * 0.065f else if (isDrizzle) w * 0.035f else w * 0.05f

    for (i in 0 until dropCount) {
        val xFrac = 0.28f + (i * 0.44f / (dropCount - 1).coerceAtLeast(1))
        val baseX = w * xFrac
        // cycle raindrops downward smoothly
        val offsetCycle = ((rainAnim + (i * 0.33f)) % 1.0f)
        val startY = h * 0.68f + offsetCycle * (h * 0.16f)
        val endY = startY + dropLen

        drawLine(
            color = rainColor,
            start = Offset(baseX, startY),
            end = Offset(baseX - (w * 0.04f), endY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSnowCloud(w: Float, h: Float, floatAnim: Float, tint: Color?) {
    val cloudColor = Color(0xFF64748B)
    val snowColor = tint ?: Color(0xFFE2E8F0)

    drawStandardCloudShape(
        centerX = w * 0.50f,
        centerY = h * 0.42f + floatAnim,
        width = w * 0.74f,
        height = h * 0.46f,
        color = cloudColor
    )

    // 3 delicate snow flakes
    listOf(
        Offset(w * 0.32f, h * 0.75f),
        Offset(w * 0.52f, h * 0.82f),
        Offset(w * 0.72f, h * 0.74f)
    ).forEach { pos ->
        drawCircle(color = snowColor, radius = w * 0.045f, center = pos)
    }
}

private fun DrawScope.drawThunderstorm(
    w: Float,
    h: Float,
    floatAnim: Float,
    rainAnim: Float,
    tint: Color?
) {
    val cloudColor = Color(0xFF475569) // Dark Storm Cloud
    val boltColor = Color(0xFFFBBF24)  // Golden Lightning
    val rainColor = tint ?: Color(0xFF38BDF8)

    drawStandardCloudShape(
        centerX = w * 0.50f,
        centerY = h * 0.40f + floatAnim,
        width = w * 0.76f,
        height = h * 0.46f,
        color = cloudColor
    )

    // Rain drop left & right
    drawLine(
        color = rainColor,
        start = Offset(w * 0.28f, h * 0.72f),
        end = Offset(w * 0.24f, h * 0.86f),
        strokeWidth = w * 0.045f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = rainColor,
        start = Offset(w * 0.74f, h * 0.72f),
        end = Offset(w * 0.70f, h * 0.86f),
        strokeWidth = w * 0.045f,
        cap = StrokeCap.Round
    )

    // Central sharp Lightning Bolt Path
    val boltPath = Path().apply {
        moveTo(w * 0.52f, h * 0.56f)
        lineTo(w * 0.42f, h * 0.73f)
        lineTo(w * 0.50f, h * 0.73f)
        lineTo(w * 0.44f, h * 0.94f)
        lineTo(w * 0.60f, h * 0.71f)
        lineTo(w * 0.51f, h * 0.71f)
        close()
    }
    drawPath(path = boltPath, color = boltColor)
}

// --- Primitive Cloud Drawing Helpers ---

private fun DrawScope.drawStandardCloudShape(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val rBase = height * 0.28f
    val baseY = centerY + height * 0.16f

    // 1. Base pill shape
    val leftBase = centerX - width * 0.32f
    val rightBase = centerX + width * 0.32f

    // Draw pill foundation
    drawLine(
        color = color,
        start = Offset(leftBase, baseY),
        end = Offset(rightBase, baseY),
        strokeWidth = rBase * 2f,
        cap = StrokeCap.Round
    )

    // 2. Main Center Dome
    drawCircle(
        color = color,
        radius = height * 0.40f,
        center = Offset(centerX - width * 0.04f, centerY - height * 0.04f)
    )

    // 3. Left secondary dome
    drawCircle(
        color = color,
        radius = height * 0.30f,
        center = Offset(centerX - width * 0.24f, centerY + height * 0.04f)
    )

    // 4. Right secondary dome
    drawCircle(
        color = color,
        radius = height * 0.28f,
        center = Offset(centerX + width * 0.20f, centerY + height * 0.06f)
    )
}

private fun DrawScope.drawCompactCloudPuff(
    center: Offset,
    scale: Float,
    cloudColor: Color
) {
    val r = scale * 0.45f
    // Small bottom pill
    drawLine(
        color = cloudColor,
        start = Offset(center.x - scale * 0.4f, center.y + scale * 0.15f),
        end = Offset(center.x + scale * 0.4f, center.y + scale * 0.15f),
        strokeWidth = r * 1.5f,
        cap = StrokeCap.Round
    )
    // Small top puffed circle
    drawCircle(
        color = cloudColor,
        radius = r * 1.15f,
        center = Offset(center.x, center.y - scale * 0.10f)
    )
    drawCircle(
        color = cloudColor,
        radius = r * 0.90f,
        center = Offset(center.x - scale * 0.26f, center.y)
    )
}
