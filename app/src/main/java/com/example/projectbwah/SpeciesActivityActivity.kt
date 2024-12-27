package com.example.projectbwah

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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
import com.example.projectbwah.data.ScheduleType
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.example.projectbwah.viewmodel.SpeciesActivityViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId


class SpeciesActivityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val speciesId = intent.getIntExtra("speciesId", -1).takeIf { it != -1 }
        val speciesName = intent.getStringExtra("speciesName")

        enableEdgeToEdge()
        setContent {
            ProjectBWAHTheme {
                SpeciesActivityScreen(speciesId, speciesName)
            }
        }
    }
}

@Composable
fun SpeciesActivityScreen(
    speciesId: Int?,
    speciesName: String?,
    viewModel: SpeciesActivityViewModel = viewModel()
) {
    val speciesActivities by viewModel.speciesActivities.collectAsState(emptyList())
    val editingActivity by viewModel.editingActivity.collectAsState()
    var showAddActivity by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedActivities = remember { mutableStateListOf<DefaultActivity>() }
    val coroutineScope = rememberCoroutineScope()

    viewModel.getSpeciesActivities(speciesId)




    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        },
        floatingActionButton = {
            Button(onClick = { showAddActivity = true }) {
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
                contentPadding = PaddingValues(8.dp)
            ) {
                items(speciesActivities) { activity ->
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

        if (showAddActivity) {
            ActivityDialog(
                onDismissRequest = { showAddActivity = false },
                activityId = null,
                speciesId = speciesId,
                petId = null,
                isPetActivity = false,
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
            ActivityDialog(
                onDismissRequest = { viewModel.onEditActivity(null) }, // Clear editing state on dismiss
                activityId = editingActivity?.id,
                speciesId = speciesId,
                petId = null,
                isPetActivity = false
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

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AddActivityBottomSheet(
//    onDismiss: () -> Unit,
//    onAddActivity: (DefaultActivity) -> Unit,
//    viewModel: SpeciesActivityViewModel = viewModel(),
//    speciesId: Int
//) {
//    var activityName by viewModel.activityName
//    val scheduleType by viewModel.selectedScheduleType
//    var scheduleTime by viewModel.scheduleTime
//    var scheduleDayOfWeekOrMonth by viewModel.scheduleDayOfWeekOrMonth
//    var scheduleDate by viewModel.scheduleDate
//    var isDefault by viewModel.isDefault
//
//    ModalBottomSheet(
//        onDismissRequest = onDismiss
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row {
//                OutlinedTextField(
//                    value = activityName,
//                    onValueChange = { viewModel.onActivityNameChange(it) },
//                    label = { Text("Activity Name") }
//                )
//
//                Row {
//                    Text("Is Default")
//                    Checkbox(
//                        checked = isDefault,
//                        onCheckedChange = { viewModel.onIsDefaultChange(it) },
//                    )
//                }
//            }
//
//            // Schedule Type Selection
//            var expanded by remember { mutableStateOf(false) }
//            val scheduleTypes = listOf(ScheduleType.DAILY, ScheduleType.WEEKLY, ScheduleType.ONCE)
//            var selectedScheduleType by viewModel.selectedScheduleType
//
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                TextField(
//                    value = selectedScheduleType?.name ?: "Select a Schedule Type",
//                    onValueChange = {},
//                    readOnly = true,
//                    modifier = Modifier
//                        .menuAnchor()
//                        .fillMaxWidth()
//                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
//                    trailingIcon = {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                    }
//                )
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    scheduleTypes.forEach { scheduleType ->
//                        DropdownMenuItem(
//                            text = { Text(scheduleType.name) },
//                            onClick = {
//                                selectedScheduleType = scheduleType
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//            // State for Time and Date pickers
//            val timePickerState = rememberTimePickerState()
//            val datePickerState = rememberDatePickerState()
//
////            val isAddButtonEnabled = when (scheduleType) {
////                ScheduleType.DAILY -> !activityName.isBlank() && scheduleTime != null
////                ScheduleType.WEEKLY -> !activityName.isBlank() && scheduleDayOfWeek != null && scheduleTime != null
////                ScheduleType.ONCE -> !activityName.isBlank() && scheduleDate != null && scheduleTime != null
////                else -> false
////            }
//
//
//            // Conditional UI Elements based on Schedule Type
//            when (scheduleType) {
//
//                ScheduleType.ONCE -> {
//
//                    // Date Picker
//                    DatePicker(
//                        state = datePickerState,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    // Time Picker
//                    TimePicker(
//                        state = timePickerState,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//
//                ScheduleType.DAILY -> {
//                    // Time Picker
//                    TimePicker(
//                        state = timePickerState,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//
//                ScheduleType.WEEKLY -> {
//
//                    // Schedule Day of Week Dropdown
//                    var expandedDayOfWeek by remember { mutableStateOf(false) }
//                    val daysOfWeek = listOf(
//                        "Sunday",
//                        "Monday",
//                        "Tuesday",
//                        "Wednesday",
//                        "Thursday",
//                        "Friday",
//                        "Saturday"
//                    )
//
//                    ExposedDropdownMenuBox(
//                        expanded = expandedDayOfWeek,
//                        onExpandedChange = { expandedDayOfWeek = !expandedDayOfWeek }
//                    ) {
//                        TextField(
//                            readOnly = true,
//                            value = scheduleDayOfWeekOrMonth?.let { daysOfWeek[it - 1] }
//                                ?: "Select a Day",
//                            onValueChange = {},
//                            label = { Text("Day of Week") },
//                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDayOfWeek) },
//                            modifier = Modifier
//                                .menuAnchor()
//                                .fillMaxWidth(),
//                            colors = ExposedDropdownMenuDefaults.textFieldColors()
//                        )
//                        ExposedDropdownMenu(
//                            expanded = expandedDayOfWeek,
//                            onDismissRequest = { expandedDayOfWeek = false }
//                        ) {
//                            daysOfWeek.forEachIndexed { index, day ->
//                                DropdownMenuItem(
//                                    text = { Text(day) },
//                                    onClick = {
//                                        viewModel.onScheduleDayOfWeekChange(index + 1)
//                                        expandedDayOfWeek = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//
//                ScheduleType.MONTHLY -> {
//                    // Date Picker
//                    DatePicker(
//                        state = datePickerState,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    // Time Picker
//                    TimePicker(
//                        state = timePickerState,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // "Add" Button
//            Button(
//                onClick = {
//                    scheduleTime =
//                        LocalTime.of(timePickerState.hour ?: 0, timePickerState.minute ?: 0)
//                    scheduleDate = datePickerState.selectedDateMillis?.let {
//                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
//                    }
//
//                    val newActivity = DefaultActivity(
//                        name = activityName,
//                        speciesId = speciesId,
//                        scheduleType = scheduleType,
//                        scheduleTime = scheduleTime,
//                        scheduleDayOfWeekOrMonth = scheduleDayOfWeekOrMonth,
//                        isDefault = isDefault
//                    )
//                    onAddActivity(newActivity)
//                    onDismiss() // Close the bottom sheet after adding
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Add")
//            }
//        }
//    }
//}
