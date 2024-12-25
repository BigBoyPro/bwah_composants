package com.example.projectbwah.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.data.Pet
import com.example.projectbwah.viewmodel.MainViewModel
import com.example.projectbwah.PetDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(pets: List<Pet>, viewModel: MainViewModel = viewModel()) {


    val selectedPets = viewModel.selectedPets
    var showDeleteConfirmationDialog by viewModel.showDeleteConfirmationDialog
    var showPetDialog by rememberSaveable { mutableStateOf(false) }
    var selectedPetId by rememberSaveable { mutableStateOf<Int?>(null) }


    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier.fillMaxSize() // Ensure the grid fills the entire screen
    ) {
        items(pets) {pet ->
            val isSelected = pet in selectedPets
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
                    .combinedClickable(
                        onClick = {
                            selectedPetId = pet.idPet
                            showPetDialog = true
                        },
                        onLongClick = {
                            if (isSelected) {
                                selectedPets.remove(pet)
                            } else {
                                selectedPets.add(pet)
                            }
                        }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {

                Column( modifier = Modifier.fillMaxSize( ) ,  horizontalAlignment = Alignment.CenterHorizontally,  verticalArrangement = Arrangement.SpaceBetween ) {
                    Box( contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth( )  ) {
                        Icon( imageVector = Icons.Default.Person,  contentDescription = "Add", modifier = Modifier.size(75.dp) ,  tint = MaterialTheme.colorScheme. onSecondaryContainer ) }
                    Text( text = pet.name, modifier = Modifier.padding(8.dp) ,  color = MaterialTheme.colorScheme. onPrimary )
                }

            }
        }
        item {
            Card(
                modifier = Modifier
                    .width(150.dp)
                    .height(165.dp)
                    .padding(8.dp)
                    .padding(bottom = 15.dp)
                    .clickable {
                        selectedPetId = null
                        showPetDialog = true
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

    }
    if (showPetDialog) {
        PetDialog(petId = selectedPetId, onDismissRequest = { showPetDialog = false })
    }

    // Delete button
    if (selectedPets.isNotEmpty()) {
        Button(modifier = Modifier.padding(16.dp), onClick = { showDeleteConfirmationDialog = true }, colors = ButtonColors(containerColor = Color.Red, contentColor = Color.White, disabledContainerColor = Color.Red, disabledContentColor = Color.White)) {
            Text("Delete Selected")
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Delete Pets") },
            text = { Text("Are you sure you want to delete the selected pets?") },
            confirmButton = {
                TextButton(onClick = {
                    // Delete selected pets
                    viewModel.deleteSelectedPets() // Call the ViewModel function
                    showDeleteConfirmationDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}

//@Composable
//fun HomeScreen(pets: List<Pet>) {
//    val context = LocalContext.current
//    LazyVerticalGrid(
//    columns = GridCells.Adaptive(minSize = 150.dp),
//    modifier = Modifier.fillMaxSize( )  // Ensure the grid fills the entire screen     )
//    { items(pets) { pet ->
//        Card( modifier = Modifier.size(150.dp).padding(8.dp).clickable {
//            val editPetIntent = Intent(context, AddPetActivity::class. java)
//            editPetIntent.putExtra( " petId" ,  pet.idPet)
//            context.startActivity( editPetIntent)  },
//            colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme. primary)  )
//
//        {
//            Column( modifier = Modifier.fillMaxSize( ) ,  horizontalAlignment = Alignment.CenterHorizontally,  verticalArrangement = Arrangement.SpaceBetween ) {
//                Box( contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth( )  ) {
//                    Icon( imageVector = Icons.Default.Person,  contentDescription = "Add", modifier = Modifier.size(75.dp) ,  tint = MaterialTheme.colorScheme. onSecondaryContainer ) }
//                Text( text = pet.name, modifier = Modifier.padding(8.dp) ,  color = MaterialTheme.colorScheme. onPrimary )
//            }
//        }
//    }
//        item { Card( modifier = Modifier .width(150.dp) .height(165.dp) .padding(8.dp) .padding(bottom = 15.dp) .clickable { val addPetIntent = Intent(context, AddPetActivity::class. java)  context.startActivity( addPetIntent)  }, colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme. secondary)  ) { Box( contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize( )  ) { Icon( imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(48.dp) ,  tint = MaterialTheme.colorScheme. onSecondaryContainer ) } } } } }

