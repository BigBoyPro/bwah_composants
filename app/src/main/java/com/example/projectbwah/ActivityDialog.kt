package com.example.projectbwah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.projectbwah.data.ScheduleType
import com.example.projectbwah.utils.ConfirmDismissDialog
import com.example.projectbwah.utils.CustomPopupDropdownMenu
import com.example.projectbwah.utils.DatePickerTextFieldWithError
import com.example.projectbwah.utils.TextFieldWithError
import com.example.projectbwah.utils.TimePickerTextFieldWithError
import com.example.projectbwah.viewmodel.ActivityViewModel
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDialog(
    activityId: Int?,
    speciesId: Int?,
    petId: Int?,
    editModeByDefault: Boolean = false,
    isPetActivity: Boolean,
    onDismissRequest: () -> Unit,
    viewModel: ActivityViewModel = viewModel(
        key = (activityId ?: 0).toString() + (speciesId ?: 0).toString() + (petId ?: 0).toString()
    ),
) {

    var showRevertChangesDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    val newActivity by rememberSaveable { mutableStateOf(activityId == null) }
    var editMode by rememberSaveable { mutableStateOf(newActivity || editModeByDefault) }

    val activity by viewModel.activity
    var finished by viewModel.finished

    val newOnDismissRequest = {
        if (!showRevertChangesDialog && !showDeleteConfirmationDialog && editMode) {
            if (viewModel.hasChanges() && !finished) {
                showRevertChangesDialog = true
            } else {
                viewModel.clearStates()
                if (newActivity || editModeByDefault) onDismissRequest()
                else editMode = false
            }
        } else {
            if (newActivity) viewModel.clearStates()
            onDismissRequest()
        }
    }
    if (finished) {
        if (newActivity) {
            viewModel.clearStates()
            onDismissRequest()
        } else if (editModeByDefault) {
            onDismissRequest()
        } else {
            editMode = false
        }

        finished = false
    }

    viewModel.initialize(isPetActivity, petId, speciesId)
    if (activityId != null && activity == null) {
        viewModel.loadActivity(activityId)
    }

    Dialog(
        onDismissRequest = newOnDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
        ),
    ) {

        val title = if (newActivity) "Discard Changes" else "Revert Changes"
        val text =
            if (newActivity) "Are you sure you want to discard the changes and go back?" else "Are you sure you want to revert the changes and stop editing?"
        val confirm = if (newActivity) "Discard" else "Revert"
        val dismiss = "Cancel"


        if (showRevertChangesDialog) {
            ConfirmDismissDialog(
                title = title,
                text = text,
                confirm = confirm,
                dismiss = dismiss,
                onConfirm = {
                    viewModel.clearStates()
                    if (newActivity || editModeByDefault) {
                        onDismissRequest()
                    } else {
                        editMode = false
                    }
                    showRevertChangesDialog = false
                },
                onDismiss = { showRevertChangesDialog = false }
            )
        }

        if (showDeleteConfirmationDialog) {
            ConfirmDismissDialog(
                title = "Delete Activity",
                text = "Are you sure you want to delete this activity?",
                confirm = "Delete",
                dismiss = "Cancel",
                onConfirm = {
                    if (!newActivity) {
                        onDismissRequest()
                        showDeleteConfirmationDialog = false
                        viewModel.deleteActivity()
                    }
                },
                onDismiss = { showDeleteConfirmationDialog = false }
            )
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
            modifier = Modifier
                .fillMaxSize(0.9f)
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {

                                IconButton(
                                    onClick = newOnDismissRequest,
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }

                                Text(
                                    if (newActivity) "Add Activity" else if (!editMode) viewModel.name.value else "Edit Activity",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                                if (newActivity) {
                                    IconButton(
                                        onClick = { viewModel.clearStates() },
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Clear")
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.align(Alignment.CenterEnd),
                                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                                    ) {
                                        if(!editModeByDefault) {
                                            if (!editMode) {
                                                IconButton(onClick = { editMode = true }) {
                                                    Icon(
                                                        Icons.Filled.Edit,
                                                        contentDescription = "Edit"
                                                    )
                                                }
                                            } else {
                                                IconButton(onClick = { newOnDismissRequest() }) {
                                                    Icon(
                                                        Icons.Filled.Clear,
                                                        contentDescription = "Cancel"
                                                    )
                                                }
                                            }
                                        }
                                        IconButton(onClick = {
                                            showDeleteConfirmationDialog = true
                                        }) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                tint = MaterialTheme.colorScheme.error,
                                                contentDescription = "Delete"
                                            )
                                        }
                                    }

                                }
                            }

                        }
                    )
                },
                floatingActionButton = {
                    if (editMode) {
                        FloatingActionButton(onClick = {
                            viewModel.addOrUpdateActivity()
                        }) {
                            Icon(Icons.Filled.Done, contentDescription = "Save")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ActivityScreen(
                        isPetActivity = isPetActivity,
                        editMode = editMode,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityScreen(
    isPetActivity: Boolean,
    editMode: Boolean,
    viewModel: ActivityViewModel,
) {

    var name by viewModel.name
    val nameError by viewModel.nameError
    var scheduleType by viewModel.scheduleType
    val scheduleTypeError by viewModel.scheduleTypeError
    var scheduleTime by viewModel.scheduleTime
    var scheduleDayOfWeekOrMonth by viewModel.scheduleDayOfWeekOrMonth
    var isDefault by viewModel.isDefault
    var scheduleDate by viewModel.scheduleDate
    val scheduleDateError by viewModel.scheduleDateError

    val scheduleTypes = viewModel.scheduleTypes
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 5.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Edit Activity Name
        TextFieldWithError(
            label = "Name",
            value = name,
            onValueChange = { name = it },
            isEditable = editMode,
            error = nameError
        )

        if (!isPetActivity) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Is Default")
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    enabled = editMode
                )
            }
        }

        // Schedule Type Selection

        CustomPopupDropdownMenu(
            itemsList = scheduleTypes.map { it.name },
            selectedItem = scheduleType.name,
            onItemSelected = { index, _ ->
                scheduleType = scheduleTypes[index]
            },
            label = "Schedule Type",
            placeholder = "Select a Schedule Type",
            isEditable = editMode
        )

        // Conditional UI Elements based on Schedule Type
        when (scheduleType) {
            ScheduleType.ONCE -> {
                if (isPetActivity) {
                    DatePickerTextFieldWithError(
                        label = "Date",
                        date = scheduleDate,
                        onDateSelected = { scheduleDate = it },
                        isEditable = editMode,
                        error = scheduleDateError
                    )
                }
            }

            ScheduleType.DAILY -> {}

            ScheduleType.WEEKLY -> {
                // Schedule Day of Week Dropdown
                val daysOfWeek = listOf(
                    "Sunday",
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday"
                )

                CustomPopupDropdownMenu(
                    itemsList = daysOfWeek,
                    selectedItem = scheduleDayOfWeekOrMonth?.let { daysOfWeek[it - 1] },
                    onItemSelected = { index, _ ->
                        scheduleDayOfWeekOrMonth = index + 1
                    },
                    label = "Day of Week",
                    placeholder = "Select a Day",
                    error = scheduleTypeError,
                    isEditable = editMode
                )
            }

            ScheduleType.MONTHLY -> {
                // Day of Month Picker
                val daysOfMonth = (1..31).map { it.toString() }

                CustomPopupDropdownMenu(
                    itemsList = daysOfMonth,
                    selectedItem = scheduleDayOfWeekOrMonth?.toString(),
                    onItemSelected = { index, _ ->
                        scheduleDayOfWeekOrMonth = index + 1
                    },
                    label = "Day of Month",
                    placeholder = "Select a Day",
                    error = scheduleTypeError,
                    isEditable = editMode
                )
            }
        }
        // Time Picker
        var timePickerState by remember {
            mutableStateOf(
                TimePickerState(
                    is24Hour = true,
                    initialHour = scheduleTime?.hour ?: 0,
                    initialMinute = scheduleTime?.minute ?: 0
                )
            )
        }

        LaunchedEffect(viewModel.activity.value) {
            val activity = viewModel.activity.value
            if (activity != null) {
                timePickerState = TimePickerState(
                    is24Hour = true,
                    initialHour = scheduleTime?.hour ?: 0,
                    initialMinute = scheduleTime?.minute ?: 0
                )
            }
        }

        scheduleTime = LocalTime.of(timePickerState.hour, timePickerState.minute)

        TimePickerTextFieldWithError(
            label = "Time",
            time = scheduleTime,
            timePickerState = timePickerState,
            error = scheduleTypeError,
            isEditable = editMode,
        )


    }

}

