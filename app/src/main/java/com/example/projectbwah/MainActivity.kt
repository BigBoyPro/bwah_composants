package com.example.projectbwah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.data.Pet
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.example.projectbwah.viewmodel.MainViewModel
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.projectbwah.screens.HomeScreen
import com.example.projectbwah.screens.SearchScreen
import com.example.projectbwah.screens.SettingsScreen

class MainActivity : ComponentActivity() {

    enum class NavigationBarItems(val icon: ImageVector) {
        Home(Icons.Default.Home),
        Search(Icons.Default.Search),
        Settings(Icons.Default.Settings)
    }

    private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
        composed {
            clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            splashScreen.setKeepOnScreenCondition { false }
        }
        setContent {
            ProjectBWAHTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun NavigationBarItem(item: NavigationBarItems, selectedIndex: Int, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
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

    @Composable
    private fun MainScreen(viewModel: MainViewModel = viewModel()) {
        val navigationBarItems = remember { NavigationBarItems.entries.toTypedArray() }
        var selectedIndex by remember { mutableIntStateOf(0) }
        viewModel.insertExamplePets()
        val pets by viewModel.allPets.collectAsState(emptyList())


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                AnimatedNavigationBar(
                    modifier = Modifier.height(64.dp),
                    selectedIndex = selectedIndex,
                    cornerRadius = shapeCornerRadius(cornerRadius = 35.dp),
                    barColor = MaterialTheme.colorScheme.primary,
                    ballColor = MaterialTheme.colorScheme.primary,
                    ballAnimation = Teleport(tween(300))
                ) {
                    navigationBarItems.forEach { item ->
                        NavigationBarItem(
                            item = item,
                            selectedIndex = selectedIndex,
                            onClick = { selectedIndex = item.ordinal }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = innerPadding.calculateBottomPadding() - 15.dp
                )
                .fillMaxSize()) {
                when (selectedIndex) {
                    0 -> HomeScreen(pets)
                    1 -> SearchScreen()
                    2 -> SettingsScreen()
                    else -> HomeScreen(pets)
                }
            }
        }
    }



}

