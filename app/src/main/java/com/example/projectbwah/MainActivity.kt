package com.example.projectbwah

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val spashScreen = installSplashScreen()
        spashScreen.setKeepOnScreenCondition { true }

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000L)
            spashScreen.setKeepOnScreenCondition { false }
        }
        setContent {
            ProjectBWAHTheme {
                val navigationBarItems = remember { NavigationBarItems.values() }
                var selectedIndex by remember { mutableStateOf(0) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedNavigationBar(
                            modifier =  Modifier.height(64.dp),
                            selectedIndex = selectedIndex,
                            cornerRadius = shapeCornerRadius(cornerRadius = 35.dp),
                            barColor = MaterialTheme.colorScheme.primary,
                            ballColor = MaterialTheme.colorScheme.primary,

                            ballAnimation = Teleport(tween(300))
                        ) { // Start of AnimatedNavigationBar content lambda
                            navigationBarItems.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .noRippleClickable {
                                            selectedIndex = item.ordinal
                                        },
                                    contentAlignment =  Alignment.Center

                                ) {
                                    Icon(
                                        modifier = Modifier.size(26.dp),
                                        imageVector = item.icon,
                                        contentDescription = "Bottom Navigation Bar Icon",
                                        tint = if (selectedIndex == item.ordinal) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },

                                    )
                                }
                            }
                        } // Closing curly brace for AnimatedNavigationBar content lambda

                    }
                )
                { innerPadding ->
                    when (selectedIndex) {
                        0 -> HomeScreen()
                        1 -> SearchScreen()
                        2 -> ProfileScreen()
                        else -> {HomeScreen()} // Handle default case if needed
                    }

                }

            }
        }
    }


    enum class NavigationBarItems(val icon: ImageVector) {
        Home(Icons.Default.Home),
        Search(Icons.Default.Search),
        Settings(Icons.Default.Settings)

    }

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
        clickable(indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )
        { onClick() }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        ProjectBWAHTheme {
            Greeting("Android")
        }
    }
}

@Composable
fun HomeScreen() {
    Text("Home Screen")
}

@Composable
fun SearchScreen() {
    Text("Search Screen")
}

@Composable
fun ProfileScreen() {
    Text("Profile Screen")
}

