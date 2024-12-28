package com.example.projectbwah.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.SpeciesActivitiesActivity
import com.example.projectbwah.data.Species
import com.example.projectbwah.viewmodel.MainViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by viewModel.showBottomSheet

    val speciesList by viewModel.allSpecies.collectAsState(emptyList())

    // theme helper
    val isDarkTheme by viewModel.isDarkTheme.collectAsState() // Collect theme preference from ViewModel


    Scaffold(
        floatingActionButton = {
            OutlinedButton(
                onClick = { showBottomSheet = true },
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 1f),
                )
            ) {
                Text("Add Species")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings") },
                modifier = Modifier
                    .fillMaxWidth(),
                windowInsets = WindowInsets(4.dp, 4.dp, 4.dp, 4.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
//                .verticalScroll(rememberScrollState())
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Light/Dark Mode Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)

                    ) {
                        Text("Dark Mode")
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { viewModel.toggleTheme() } // Call ViewModel function to toggle theme
                        )
                    }


                    Text("Species List", style = MaterialTheme.typography.titleMedium)
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .padding(bottom = 8.dp)
                        .clickable {
                            val iii = Intent(context, SpeciesActivitiesActivity::class.java)
                            // speciesId = null
                            iii.putExtra("speciesId", null as Int?)
                            iii.putExtra("speciesName", "Default")
                            context.startActivity(iii)
                        } // Make the Card clickable
                ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Default ")
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "For All Pets",
                                color = LocalContentColor.current.copy(alpha = 0.4f)
                            )
                        }
                }
            }

            items(speciesList) { species ->
                SpeciesListItem(species) { selectedSpecies ->
//                        Toast.makeText(context, "nouvelle valeur ${selectedSpecies.id}",
//                            Toast.LENGTH_SHORT).show()
                    val iii = Intent(context, SpeciesActivitiesActivity::class.java)
                    iii.putExtra("speciesId", selectedSpecies.id)
                    iii.putExtra("speciesName", selectedSpecies.name)

                    context.startActivity(iii)
                }
            }

            item { Spacer(modifier = Modifier.height(23.dp)) }

        }
        // Add Species Item


        // Bottom Sheet Dialog
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            )
            {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Add Species Form
                    var speciesName by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = speciesName,
                        onValueChange = { speciesName = it },
                        label = { Text("Species Name") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.addSpecies(speciesName)
                                showBottomSheet = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}


@Composable
fun SpeciesListItem(species: Species, onSpeciesClick: (Species) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onSpeciesClick(species) } // Make the Card clickable
    ) {
        Text(species.name, modifier = Modifier.padding(16.dp))
    }
}


//@Composable
//fun SpeciesActivitiesScreen(speciesId: Int, viewModel: MainViewModel = viewModel()) {
//    // Fetch species activities using viewModel and speciesId
//    val speciesActivities by viewModel.getSpeciesActivities(speciesId).collectAsState(initial = emptyList())
//
//    // Display species activities and provide options to add, delete, modify
//    LazyColumn {
//        item {
//            // Header content here
//        }
//        items(speciesActivities) { activity ->
//            Text(activity.name) // Display activity name
//            // Add buttons or other UI elements for actions
//        }
//    }
//}
