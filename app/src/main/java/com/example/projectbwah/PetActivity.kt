package com.example.projectbwah

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.example.projectbwah.viewmodel.PetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter


class AddPetActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val petId = intent.getIntExtra("petId", -1) // Get petId from intent

        setContent {
            ProjectBWAHTheme {
                AddPetScreen(petId)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(petId: Int, viewModel: PetViewModel = viewModel()) {
    var pet = viewModel.pet
    var isEdit = false;


    val context = LocalContext.current
    var name by viewModel.name
    var age by viewModel.age
    var breed by viewModel.breed
    var description by viewModel.description
    var weight by viewModel.weight
    var height by viewModel.height
    var birthDate by viewModel.birthDate
    var dateAdopted by viewModel.dateAdopted
    var color by viewModel.color
    var isMale by viewModel.isMale // Default to male
    var isSterilized by viewModel.isSterilized
    var isVaccinated by viewModel.isVaccinated
    var imageUri by viewModel.imageUri


    val speciesList by viewModel.speciesList.collectAsState() // Observe speciesList
    var selectedSpeciesName by viewModel.selectedSpeciesName

    var showErrors by viewModel.showErrors

    if (petId != -1) {
        selectedSpeciesName = viewModel.getSpeciesNameById(pet?.speciesId ?: -1)
        isEdit = true;
        viewModel.loadPet(petId)
        name = pet?.name ?: ""
        age = pet?.age?.toString() ?: ""
        breed = pet?.breed ?: ""
        description = pet?.description ?: ""
        weight = pet?.weight?.toString() ?: ""
        height = pet?.height?.toString() ?: ""
        birthDate = pet?.birthDate ?: LocalDate.now()
        dateAdopted = pet?.dateAdopted ?: LocalDate.now()
        color = pet?.color ?: ""
        isMale = pet?.isMale ?: true
        isSterilized = pet?.isSterilized ?: false
        isVaccinated = pet?.isVaccinated ?: false
        imageUri = Uri.parse(pet?.image ?: "")


    }


//    var image by viewModel.image // Handle image later




    if (showErrors) {

        MinimalDialog(onDismissRequest = { showErrors = false })

    }

    var showDatePicker by viewModel.showDatePicker
    var showAdoptedDatePicker by viewModel.showAdoptedDatePicker



    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Pet") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (isEdit) {
                    viewModel.updatePet(petId)
                } else {
                    viewModel.addPet()
                }
                if (!showErrors) {
                    (context as? ComponentActivity)?.finish()
                }

                // Navigate back or show success message
            }) {
                    Icon(Icons.Filled.Done, contentDescription = "Save")

            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            var expanded by remember { mutableStateOf(false) }


            // Species selection (replace with your implementation)



            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    label = { Text("Species") },
                    value =  selectedSpeciesName ?: "Select a Schedule Type",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    speciesList.forEach { species ->
                        DropdownMenuItem(
                            text = { Text(species.name) },
                            onClick = {
                                selectedSpeciesName = species.name
                                expanded = false
                            }
                        )
                    }
                }
            }


            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Birth Date Picker
            OutlinedTextField(
                value = birthDate.format(dateFormatter),
                onValueChange = { /* Read-only */ },
                label = { Text("Birth Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            showDatePicker = false
                            birthDate = LocalDate.now() // Replace with selected date
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker( // Add DatePicker composable here
                        state = rememberDatePickerState(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Date Adopted Picker
            OutlinedTextField(
                value = dateAdopted.format(dateFormatter),
                onValueChange = { /* Read-only */ },
                label = { Text("Date Adopted") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdoptedDatePicker = true },
                readOnly = true
            )

            if (showAdoptedDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showAdoptedDatePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            showAdoptedDatePicker = false
                            birthDate = LocalDate.now() // Replace with selected date
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showAdoptedDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker( // Add DatePicker composable here
                        state = rememberDatePickerState(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )

            // Gender selection (replace with your implementation)
            Row {
                RadioButton(selected = isMale, onClick = { isMale = true })
                Text("Male")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !isMale, onClick = { isMale = false })
                Text("Female")
            }

            // Sterilized and Vaccinated checkboxes
            Row {
                Checkbox(checked = isSterilized, onCheckedChange = { isSterilized = it })
                Text("Sterilized")
                Spacer(modifier = Modifier.width(16.dp))
                Checkbox(checked = isVaccinated, onCheckedChange = { isVaccinated = it })
                Text("Vaccinated")
            }

            // Image handling (replace with your implementation)
            // ...


            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { result ->
               imageUri = result
            }

            imagePicker(imageUri) {
                launcher.launch("image/*")
            }




            // Add the delete button


//            var showDeleteConfirmationDialog by viewModel.showDeleteConfirmationDialog
//
//            if (isEdit) { // Only show delete button in edit mode
//                IconButton(onClick = {
//                    // Show confirmation dialog before deleting
//                   showDeleteConfirmationDialog = true
//                }) {
//                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
//                }
//            }
//
//            // Delete confirmation dialog
//            if (showDeleteConfirmationDialog) {
//                AlertDialog(
//                    onDismissRequest = { showDeleteConfirmationDialog = false },
//                    title = { Text("Delete Pet") },
//                    text = { Text("Are you sure you want to delete this pet?") },
//                    confirmButton = {
//                        TextButton(onClick = {
//                            showDeleteConfirmationDialog = false
//                            viewModel.deletePet(petId)
//                            (context as? ComponentActivity)?.finish() // Close activity after deleting
//                        }) {
//                            Text("Delete")
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { showDeleteConfirmationDialog = false }) {
//                            Text("Cancel")
//                        }
//                    }
//                )
//            }



        }
    }
}


@Composable
fun  imagePicker(selectedImage: Uri?, OnImageClick: ()-> Unit) {

    if (selectedImage != null) {
        Image(
            painter = rememberAsyncImagePainter(selectedImage),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clickable { OnImageClick() }
        )
    }

    OutlinedButton(onClick = { OnImageClick() }) {
        Text("Select Image")
    }


}

@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "Please fill in all fields",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}