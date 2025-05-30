package com.example.tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.tetris.gamecore.GameManager
import com.example.tetris.ui.GameScreen
import com.example.tetris.ui.theme.TetrisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val gameManager = remember { 
                        GameManager(boardWidth = 10, boardHeight = 20).apply {
                            startGame()
                        }
                    }
                    GameScreen(
                        gameManager = gameManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}