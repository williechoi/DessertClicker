package com.example.dessertclicker.ui

import com.example.dessertclicker.model.Dessert

data class GameUiState(
    val dessertsSold: Int = 0,
    val revenue: Int = 0,
    val currentDessertPrice: Int = 0,
    val currentDessertImageId: Int = 0
)