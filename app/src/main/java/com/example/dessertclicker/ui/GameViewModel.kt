package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.dessertclicker.data.Datasource.dessertList


/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel : ViewModel() {

    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())

    // Backing property to avoid state updates from other classes.
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        resetGame()
    }

    /**
     * Re-initializes the game data to restart the game.
     */
    fun resetGame() {
        _uiState.value = GameUiState(
            revenue = 0,
            dessertsSold = 0,
            currentDessertImageId = dessertList[0].imageId,
            currentDessertPrice = dessertList[0].price
        )
    }

    /**
     * Update the revenue and desserts sold.
     * If it needs to show the next dessert, change current dessertImageId and dessertPrice.
     */
    fun onDessertClicked() {
        val revenue = _uiState.value.revenue + _uiState.value.currentDessertPrice
        val dessertsSold = _uiState.value.dessertsSold.inc()

        // Show the next dessert
        val dessertToShow = determineDessertToShow(dessertList, dessertsSold)

        // update the information
        _uiState.update { currentState ->
            currentState.copy(
                revenue = revenue,
                dessertsSold = dessertsSold,
                currentDessertImageId = dessertToShow.imageId,
                currentDessertPrice = dessertToShow.price
            )
        }
    }

    private fun determineDessertToShow(desserts: List<Dessert>, dessertsSold: Int): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                break
            }
        }
        return dessertToShow
    }

    fun shareSoldDessertsInformation(intentContext: Context) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(
                    R.string.share_text,
                    _uiState.value.dessertsSold,
                    _uiState.value.revenue
                )
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * helper method that picks a dessert from dessertList
     */


}