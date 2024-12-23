package com.example.projectbwah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.ui.theme.ProjectBWAHTheme
import com.example.projectbwah.viewmodel.PetViewModel

class EditPetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectBWAHTheme {
                val petId = intent.getIntExtra("petId", 0) // Get petId from intent
                EditPetScreen(petId)
            }
        }
    }
}

@Composable
fun EditPetScreen(petId: Int, viewModel: PetViewModel = viewModel()) {

    if (viewModel.pet == null) {
        viewModel.loadPet(petId)
    }




    // Use the same UI elements as AddPetScreen, but with pre-filled data from ViewModel
    // ... (Similar to AddPetScreen, but access data from viewModel.pet) ...
}