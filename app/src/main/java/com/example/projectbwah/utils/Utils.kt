package com.example.projectbwah.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

suspend fun saveBitmapToUri(context: Context, bitmap: Bitmap): Uri? {
    return withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.png")
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun moveFileToInternalStorage(context: Context, uri: Uri): String? {
    val file = File(context.filesDir, "pet_image_${System.currentTimeMillis()}.jpg")
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


@OptIn(ExperimentalPagerApi::class)
fun PagerState.goToNextPage(coroutineScope: CoroutineScope) {
    val nextPage = currentPage + 1
    if (nextPage < pageCount) {
        coroutineScope.launch {
            animateScrollToPage(nextPage)
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


@Composable
fun TextFieldWithError(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerTextFieldWithError(
    label: String,
    time: LocalTime?,
    timePickerState: TimePickerState,
    error: String = "",
    isEditable: Boolean,
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        .withLocale(Locale.getDefault()),
) {
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = time?.format(dateFormatter) ?: "",
        onValueChange = { /* Read-only */ },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isEditable) showTimePicker = true },
        readOnly = true,
        enabled = !isEditable,
        isError = error.isNotEmpty(),
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

    if (showTimePicker) {
        // Show the TimePicker in a dialog
        Dialog(
            onDismissRequest = { showTimePicker = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
        ) {
            Column (
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = {
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            }
        }

    }
}

@Composable
fun DatePickerTextFieldWithError(
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
            CustomDatePicker(
                date = date,
                onDateSelected = onDateSelected,
                onDismissRequest = { showDatePicker = false }
            )
        }
    }

}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomDatePicker(
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
fun CustomPopupDropdownMenu(
    itemsList: List<String>,
    selectedItem: String?,
    onItemSelected: (Int, String) -> Unit,
    label: String,
    placeholder: String,
    isEditable: Boolean = true,
    error: String = ""
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    BoxWithConstraints(contentAlignment = Alignment.TopStart) {
        val textFieldWidth = constraints.maxWidth
        val density = LocalDensity.current

        OutlinedTextField(
            value = selectedItem ?: placeholder,
            onValueChange = { /* Read-only */ },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (isEditable) expanded = true },
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
        ) {
            itemsList.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(index, item)
                        expanded = false
                    }
                )
            }
        }
    }
    if (error.isNotEmpty()) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
        )
    }
}
