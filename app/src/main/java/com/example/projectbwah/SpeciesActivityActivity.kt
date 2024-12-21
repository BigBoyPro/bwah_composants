package com.example.projectbwah

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.example.projectbwah.viewmodel.SpeciesActivityViewModel
import kotlinx.coroutines.launch


class SpeciesActivityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val speciesId = intent.getIntExtra("speciesId", -1)
        val speciesName = intent.getStringExtra("speciesName")

        enableEdgeToEdge()
        setContent {
            ProjectBWAHTheme {
                SpeciesActivityScreen(speciesId, speciesName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeciesActivityScreen(speciesId: Int, speciesName: String?,viewModel: SpeciesActivityViewModel = viewModel()) {
    val speciesActivities by viewModel.speciesActivities.collectAsState(emptyList())
    val editingActivity by viewModel.editingActivity.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedActivities = remember { mutableStateListOf<DefaultActivity>() }
    val coroutineScope = rememberCoroutineScope()

    if (speciesId != -1) {
        viewModel.getSpeciesActivities(speciesId)
    } else {
        Log.e("SpeciesActivityScreen", "Invalid speciesId: $speciesId")
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        },
        floatingActionButton = {
            Button(onClick = { showBottomSheet = true }) {
                Text("Add Activity")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = "$speciesName Activities",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) { items(speciesActivities) { activity ->
                    ActivityItem(
                        activity = activity,
                        isSelected = activity in selectedActivities,
                        onItemClick = {
                            if (activity in selectedActivities) {
                                selectedActivities.remove(activity)
                            } else {
                                selectedActivities.add(activity)
                            }
                        },
                        onEditClick = {
                            viewModel.onEditActivity(activity)
                        }
                    )
                }
            }

            if (selectedActivities.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        if (showBottomSheet) {
            AddActivityBottomSheet(
                onDismiss = { showBottomSheet = false },
                onAddActivity = { activityName ->
                    val newActivity = DefaultActivity(
                        name = activityName,
                        speciesId = speciesId,
                        scheduleType = null, // Set appropriate values
                        scheduleTime = null,
                        scheduleDayOfWeek = null,
                        scheduleDate = null,
                        isDefault = false
                    )
                    viewModel.addActivity(newActivity)
                    showBottomSheet = false

                }
            )
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete the selected activities?") },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            selectedActivities.forEach { activity ->
                                viewModel.deleteActivity(activity)
                            }
                            selectedActivities.clear()
                            showDeleteConfirmation = false
                        }
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (editingActivity != null) {
            EditActivityDialog(
                activity = editingActivity!!,
                onDismiss = { viewModel.onEditActivity(null) }, // Clear editing state on dismiss
                onSave = { updatedActivity ->
                    viewModel.updateActivity(updatedActivity)
                }
            )
        }
    }
}

@Composable
fun ActivityItem(
    activity: DefaultActivity,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onEditClick: (activity: DefaultActivity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onItemClick() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(activity.name)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEditClick(activity) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityBottomSheet(
    onDismiss: () -> Unit,
    onAddActivity: (String) -> Unit
) {
    var activityName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = activityName,
                onValueChange = { activityName = it },
                label = { Text("Activity Name") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onAddActivity(activityName) }) {
                Text("Add")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityDialog(
    activity: DefaultActivity,
    onDismiss: () -> Unit,
    onSave: (DefaultActivity) -> Unit
) {
    var editedName by remember { mutableStateOf(activity.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Activity") },
        text = {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Activity Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedActivity = activity.copy(name = editedName)
                onSave(updatedActivity)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}