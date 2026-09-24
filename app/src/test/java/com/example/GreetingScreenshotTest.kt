package com.example

import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.AppLanguage
import com.example.model.ForecastCategory
import com.example.model.WeatherModel
import com.example.ui.components.ModelSelectorBar
import com.example.ui.theme.WeatherAppTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      WeatherAppTheme {
        Surface {
          ModelSelectorBar(
            selectedCategory = ForecastCategory.SHORT_TERM,
            onCategorySelected = {},
            selectedModel = WeatherModel.ECMWF_IFS,
            onModelSelected = {},
            onShowModelInfo = {},
            lang = AppLanguage.ENGLISH
          )
        }
      }
    }

    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
