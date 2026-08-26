package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.domain.calculator.HijrahDateConverter
import com.example.ui.components.ProminentHijriHeroCard
import com.example.ui.theme.DeenMateTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun prominentHijriHeroCard_screenshot() {
    val sampleHijri = HijrahDateConverter.fromLocalDate(LocalDate.of(2026, 8, 26))
    composeTestRule.setContent {
      DeenMateTheme {
        ProminentHijriHeroCard(
          hijriDate = sampleHijri,
          gregorianDateText = "Wednesday, 26 August 2026",
          cityName = "Makkah",
          hijriAdjustment = 0,
          onViewCalendar = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/hijri_card.png")
  }
}
