package com.example.projectbwah

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.viewmodel.PetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberAsyncImagePainter
import com.example.projectbwah.data.Species
import com.example.projectbwah.utils.ImageCropper
import com.example.projectbwah.utils.saveBitmapToUri
import com.exyte.animatednavbar.utils.toDp
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.FormatStyle
import java.util.Locale


@Composable
fun PetDialog(
    petId: Int?, onDismissRequest: () -> Unit,
    viewModel: PetViewModel = viewModel(key = petId?.toString() ?: "newPet")
) {
    var showRevertChangesDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirmationDialog by rememberSaveable { mutableStateOf(false) }

    val newPet by rememberSaveable { mutableStateOf(petId == null) }
    var editMode by rememberSaveable { mutableStateOf(petId == null) }

    val pet by viewModel.pet
    var finished by viewModel.finished

    val newOnDismissRequest = {
        if (!showRevertChangesDialog && !showDeleteConfirmationDialog && editMode) {
            if (viewModel.hasChanges() && !finished) {
                showRevertChangesDialog = true
            } else {
                viewModel.clearStates()
                if (newPet) onDismissRequest()
                else editMode = false
            }
        } else {
            if (newPet) viewModel.clearStates()
            onDismissRequest()
        }
    }
    if (finished) {
        if (newPet) {
            viewModel.clearStates()
            onDismissRequest()
        } else editMode = false

        finished = false
    }

    if (petId != null && pet == null) {
        viewModel.loadPet(petId)
    }


    Popup(
        onDismissRequest = newOnDismissRequest,
        properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
        alignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
            modifier = Modifier
                .fillMaxSize(0.9f)

        ) {

            PetScreen(
                petId,
                newOnDismissRequest,
                newPet,
                editMode,
                { editMode = it },
                showRevertChangesDialog,
                { showRevertChangesDialog = it },
                showDeleteConfirmationDialog,
                { showDeleteConfirmationDialog = it },
                viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetScreen(
    petId: Int?,
    onDismissRequest: () -> Unit,
    newPet: Boolean,
    editMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    showRevertChangesDialog: Boolean,
    onShowRevertChangesDialog: (Boolean) -> Unit,
    showDeleteConfirmationDialog: Boolean,
    onShowDeleteConfirmationDialog: (Boolean) -> Unit,
    viewModel: PetViewModel = viewModel(key = petId?.toString() ?: "newPet")
) {

    var name by viewModel.name
    val nameError by viewModel.nameError
    var breed by viewModel.breed
    val breedError by viewModel.breedError
    var description by viewModel.description
    val descriptionError by viewModel.descriptionError
    var weight by viewModel.weight
    val weightError by viewModel.weightError
    var height by viewModel.height
    val heightError by viewModel.heightError
    var birthDate by viewModel.birthDate
    val birthDateError by viewModel.birthDateError
    var adoptedDate by viewModel.adoptedDate
    val adoptedDateError by viewModel.adoptedDateError
    var color by viewModel.color
    val colorError by viewModel.colorError
    var isMale by viewModel.isMale
    var isSterilized by viewModel.isSterilized
    var imageUri by viewModel.imageUri


    val speciesList by viewModel.speciesList.collectAsState() // Observe speciesList
    var selectedSpeciesName by viewModel.selectedSpeciesName


    var dialogOffset by remember { mutableStateOf(Offset.Zero) }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionOnScreen()
                dialogOffset = position
            },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {

                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }

                        Text(
                            if (newPet) "Add Pet" else if (!editMode) name else "Edit Pet",
                            modifier = Modifier.align(Alignment.Center)
                        )
                        if (newPet) {
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
                                if (!editMode) {
                                    IconButton(onClick = { onEditModeChange(true) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                    }
                                } else {
                                    IconButton(onClick = { onDismissRequest() }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Cancel")
                                    }
                                }
                                IconButton(onClick = { onShowDeleteConfirmationDialog(true) }) {
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
                    viewModel.addOrUpdatePet()
                }) {
                    Icon(Icons.Filled.Done, contentDescription = "Save")
                }
            }
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp, 5.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            ImagePicker(
                imageUri = imageUri,
                isEditable = editMode,
                onImageUri = { imageUri = it }
            )

            TextFieldWithError(
                label = "Name",
                value = name,
                onValueChange = { name = it },
                isEditable = editMode,
                error = nameError
            )

            // Species selection (replace with your implementation)
            // DropdownMenu for species selection

            CustomDropdownMenu(
                speciesList = speciesList,
                selectedItem = selectedSpeciesName,
                onItemSelected = { selectedSpeciesName = it },
                isEditable = editMode,
                dialogOffset = dialogOffset
            )

            TextFieldWithError(
                label = "Breed",
                value = breed,
                onValueChange = { breed = it },
                isEditable = editMode,
                error = breedError
            )

            TextFieldWithError(
                label = "Description",
                value = description,
                onValueChange = { description = it },
                isEditable = editMode,
                error = descriptionError
            )

            TextFieldWithError(
                label = "Weight",
                value = weight,
                onValueChange = { weight = it },
                isEditable = editMode,
                isNumber = true,
                error = weightError
            )

            TextFieldWithError(
                label = "Height",
                value = height,
                onValueChange = { height = it },
                isEditable = editMode,
                isNumber = true,
                error = heightError
            )

            DatePickerTextFieldWithError(
                label = "Birth Date",
                date = birthDate,
                onDateSelected = { birthDate = it },
                isEditable = editMode,
                error = birthDateError,
            )

            // Date Adopted Picker
            DatePickerTextFieldWithError(
                label = "Adopted Date",
                date = adoptedDate,
                onDateSelected = { adoptedDate = it },
                isEditable = editMode,
                error = adoptedDateError,
            )

            TextFieldWithError(
                "Color",
                value = color,
                onValueChange = { color = it },
                isEditable = editMode,
                error = colorError
            )

            // Gender selection (replace with your implementation)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isMale,
                        onClick = { isMale = true },
                        enabled = editMode
                    )
                    Text("Male")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isMale,
                        onClick = { isMale = false },
                        enabled = editMode
                    )
                    Text("Female")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isSterilized,
                        onCheckedChange = { isSterilized = it },
                        enabled = editMode
                    )
                    Text("Sterilized")
                }

            }

            val title = if (newPet) "Discard Changes" else "Revert Changes"
            val text =
                if (newPet) "Are you sure you want to discard the changes and go back?" else "Are you sure you want to revert the changes and stop editing?"
            val confirm = if (newPet) "Discard" else "Revert"
            val dismiss = "Cancel"

            if (showRevertChangesDialog) {
                ConfirmDismissDialog(
                    title = title,
                    text = text,
                    confirm = confirm,
                    dismiss = dismiss,
                    onConfirm = {
                        viewModel.clearStates()
                        if (newPet) {
                            onDismissRequest()
                        } else {
                            onEditModeChange(false)
                        }
                        onShowRevertChangesDialog(false)
                    },
                    onDismiss = { onShowRevertChangesDialog(false) }
                )
            }

            if (showDeleteConfirmationDialog) {
                ConfirmDismissDialog(
                    title = "Delete Pet",
                    text = "Are you sure you want to delete this pet?",
                    confirm = "Delete",
                    dismiss = "Cancel",
                    onConfirm = {
                        if (petId != null) {
                            onDismissRequest()
                            onShowDeleteConfirmationDialog(false)
                            viewModel.deletePet()
                        }
                    },
                    onDismiss = { onShowDeleteConfirmationDialog(false) }
                )
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomDropdownMenu(
    speciesList: List<Species>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    isEditable: Boolean,
    dialogOffset: Offset
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    BoxWithConstraints {
        val textFieldWidth = constraints.maxWidth
        var offset = Offset.Zero
        val density = LocalDensity.current

        Box(contentAlignment = Alignment.TopStart) {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = { /* Read-only */ },
                label = { Text("Species") },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (isEditable) expanded = true }
                    .pointerInteropFilter {
                        offset = Offset(it.x, it.y)
                        false
                    },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                    disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
                    disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                    disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().unfocusedLeadingIconColor,
                    disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,
                    disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                    disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                    disabledSupportingTextColor = OutlinedTextFieldDefaults.colors().unfocusedSupportingTextColor,
                    disabledPrefixColor = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor,
                    disabledSuffixColor = OutlinedTextFieldDefaults.colors().unfocusedSuffixColor,
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(density) { textFieldWidth.toDp() }),
                offset = DpOffset(
                    (offset.x + dialogOffset.x).toDp(),
                    (offset.y + dialogOffset.y).toDp()
                )
            ) {
                // add Empty item
                speciesList.forEach { species ->
                    DropdownMenuItem(
                        text = { Text(species.name) },
                        onClick = {
                            onItemSelected(species.name)
                            expanded = false
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Other") },
                    onClick = {
                        onItemSelected("Other")
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TextFieldWithError(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean,
    isNumber: Boolean = false,
    error: String = ""
) {

    if (isEditable || value.isNotBlank()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text),
            readOnly = !isEditable
        )
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun DatePickerTextFieldWithError(
    label: String,
    date: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    isEditable: Boolean,
    error: String = "",
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault()),
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (isEditable || date != null) {
        OutlinedTextField(
            value = date?.format(dateFormatter) ?: "",
            onValueChange = { /* Read-only */ },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (isEditable) showDatePicker = true },
            readOnly = true,
            enabled = !isEditable,

            // reset colors to enabled
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                disabledContainerColor = OutlinedTextFieldDefaults.colors().unfocusedContainerColor,
                disabledBorderColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().unfocusedLeadingIconColor,
                disabledTrailingIconColor = OutlinedTextFieldDefaults.colors().unfocusedTrailingIconColor,
                disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                disabledPlaceholderColor = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                disabledSupportingTextColor = OutlinedTextFieldDefaults.colors().unfocusedSupportingTextColor,
                disabledPrefixColor = OutlinedTextFieldDefaults.colors().unfocusedPrefixColor,
                disabledSuffixColor = OutlinedTextFieldDefaults.colors().unfocusedSuffixColor,
            )
        )
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (showDatePicker) {
            DatePicker(
                date = date,
                onDateSelected = onDateSelected,
                onDismissRequest = { showDatePicker = false }
            )
        }
    }

}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DatePicker(
    date: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    datePickerState.selectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)
        ?.toInstant()
        ?.toEpochMilli()

    DatePickerDialog(

        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                onDateSelected(
                    datePickerState.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                    },
                )
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }

    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ImagePicker(
    imageUri: Uri?,
    onImageUri: (Uri?) -> Unit,
    aspectRatio: Float = 16f / 9f,
    isEditable: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    var onPickImage by remember { mutableStateOf({}) }


    if(isEditable) {
        ImageCropper(
            aspectRatio = aspectRatio,
            context = context,
            onImage = {
                coroutineScope.launch {
                    val uri = saveBitmapToUri(context, it.asAndroidBitmap())
                    onImageUri(uri)
                }
            },
            onOnPickImage = { onPickImage = it }
        )

    }

    if (imageUri != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .clip(MaterialTheme.shapes.medium)
                .then(if (isEditable) Modifier.clickable { onPickImage() } else Modifier)
        )
    }else{
        if(isEditable) {
            IconButton(onClick = onPickImage) {
                Icon(Icons.Filled.AccountBox, contentDescription = "Add Image")
            }
        }
    }
}
@Composable
fun ConfirmDismissDialog(
    title: String,
    text: String,
    confirm: String,
    dismiss: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirm)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismiss)
            }
        }
    )
}



