package com.example.fetchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider

import com.example.fetchapp.ui.AppScreen
import com.example.fetchapp.viewModel.ItemsViewModel


class MainActivity : ComponentActivity() {
    private  val viewModel : ItemsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppScreen(viewModel) // The main screen is called
        }
    }
}


