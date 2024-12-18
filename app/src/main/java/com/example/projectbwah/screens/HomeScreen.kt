package com.example.projectbwah.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projectbwah.data.Pet
import com.example.projectbwah.viewmodel.MainViewModel

@Composable
fun HomeScreen(pets: List<Pet>, viewModel: MainViewModel = viewModel()) {
    Column {
        Text("Home Screen")

        LazyColumn {
            items(pets) { pet ->
                Text(pet.name ?: "Unknown Pet")
            }
        }

        Button(onClick = { }) {
            Text("Add Pet")
        }


    }
}


