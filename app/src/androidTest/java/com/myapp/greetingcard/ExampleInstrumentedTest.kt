package com.myapp.greetingcard

import Navigator
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class MyComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun myTest() {
        // Start the app
//        composeTestRule.setContent {
//            //MyAppTheme {
//                Navigator()
//            //}
//        }

        composeTestRule.onNodeWithText("An Nam").assertIsDisplayed()

    }
    @Test
    fun backButtonAddCard() {
        // Start the app
//        composeTestRule.setContent {
//            //MyAppTheme {
//            Navigator()
//            //}
//        }

        composeTestRule.onNodeWithText("Add Card").performClick()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()


    }
    @Test
    fun goBackTest(){
//        composeTestRule.setContent {
//            Navigator()
//        }
        composeTestRule.onNodeWithText("Add Card").performClick()
        composeTestRule.onNodeWithText("Back").assertExists().performClick()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()

    }

    @Test
    fun homeStartDestination(){
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        composeTestRule.setContent {
            Navigator(navController)



        }

        assertEquals("home", navController.currentDestination?.route)

    }

    @Test
    fun homeScreen_whenStudyCardButtonClicked_navigatesToStudyCardScreen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        composeTestRule.setContent {
            // Use the test controller you created
            Navigator(navController)
        }
        composeTestRule.runOnUiThread {
            navController.navigate("home")

        }

        composeTestRule
            .onNodeWithContentDescription("navigateToStusyCard")
            .assertTextEquals("Study Cards")
            .performClick()

        // 3. ASSERT: Check that the current route is now the study card screen
        assertEquals("study_cards", navController.currentDestination?.route)


    }
}

//    @Test
//    fun homeScreenTest(){
//        composeTestRule.setContent {
//            Navigator()
//        }
//        HomeScreen(
//            innerPadding = 12.dp,
//            navigator: NavHostController
//        )
//    }

