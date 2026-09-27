package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verifyModelCategories() {
    val forecastModels = com.example.model.WeatherModel.forCategory(com.example.model.ForecastCategory.FORECAST)
    val expectedForecast = listOf(
      com.example.model.WeatherModel.ICON_EU_FLASH,
      com.example.model.WeatherModel.WEATHERNEXT_3,
      com.example.model.WeatherModel.ICON_EU,
      com.example.model.WeatherModel.ECMWF_IFS
    )
    assertEquals(expectedForecast, forecastModels)

    val longTermModels = com.example.model.WeatherModel.forCategory(com.example.model.ForecastCategory.LONG_TERM)
    val expectedLongTerm = listOf(
      com.example.model.WeatherModel.ECMWF_AIFS,
      com.example.model.WeatherModel.ECMWF_EXTENDED,
      com.example.model.WeatherModel.GEFS,
      com.example.model.WeatherModel.SEAS5
    )
    assertEquals(expectedLongTerm, longTermModels)
    assertFalse(longTermModels.contains(com.example.model.WeatherModel.CFSV2))
    assertFalse(longTermModels.contains(com.example.model.WeatherModel.GFS))
  }
}
