package com.example.tetris.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tetris.gamecore.GameManager
import com.example.tetris.gamecore.GameBoardState
import com.example.tetris.gamecore.GameState
import com.example.tetris.gamecore.Point
import com.example.tetris.gamecore.Tetromino
import com.example.tetris.gamecore.TetrominoShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive

// Default cell size, can be adjusted or passed via theme
val DefaultCellSize = 20.dp
val PreviewCellSize = 15.dp // Smaller for next piece preview

// Re-import getComposeColor if it's in BoardComposables.kt and this file is separate.
// Assuming BoardComposables.kt and GameScreenComposables.kt are in the same package `com.example.tetris.ui`
// then getComposeColor, GameBoardView, and TetrominoView are directly accessible.

/**
 * Displays the current game score.
 * @param score The current score.
 */
@Composable
fun ScoreView(score: Long, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Score", style = MaterialTheme.typography.labelSmall)
        Text(score.toString(), style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Displays the next Tetromino piece.
 * @param tetromino The next Tetromino to display.
 * @param cellSize The size for rendering the cells of the preview Tetromino.
 */
@Composable
fun NextPieceView(tetromino: Tetromino?, cellSize: Dp, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Next", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(2.dp))
        // Define a fixed size Box for the preview area
        Box(modifier = Modifier
            .size(width = cellSize * 4f, height = cellSize * 3f) // More compact height
            .border(1.dp, Color.LightGray)
        ) {
            tetromino?.let {
                // Adjust position for preview display (center it)
                val previewPiece = it.copy(
                    position = Point(
                        x = if (it.shape == TetrominoShape.I) 0 else 1, // I is wider
                        y = if (it.shape == TetrominoShape.I || it.shape == TetrominoShape.O) 0 else 0
                    )
                )
                TetrominoView(tetromino = previewPiece, cellSize = cellSize)
            }
        }
    }
}

/**
 * Displays the current game level.
 * @param level The current level.
 */
@Composable
fun LevelView(level: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Level", style = MaterialTheme.typography.labelSmall)
        Text(level.toString(), style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Displays game control buttons.
 */
@Composable
fun GameControlsView(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onRotate: () -> Unit,
    onDrop: () -> Unit,
    onTogglePause: () -> Unit,
    currentGameState: GameState, // To change Pause/Play text
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top row: Movement and action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onMoveLeft,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) { 
                Text("←", style = MaterialTheme.typography.titleLarge) 
            }
            Button(
                onClick = onRotate,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) { 
                Text("↻", style = MaterialTheme.typography.titleLarge) 
            }
            Button(
                onClick = onMoveRight,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) { 
                Text("→", style = MaterialTheme.typography.titleLarge) 
            }
        }
        
        // Bottom row: Drop and Pause buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onDrop,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) { 
                Text("Drop", style = MaterialTheme.typography.titleMedium) 
            }
            Button(
                onClick = onTogglePause,
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                Text(
                    if (currentGameState == GameState.PLAYING) "Pause" else "Play",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// --- Preview Functions ---
// Previews will still work, but they won't be "live" with StateFlow updates
// unless you create a CoroutineScope and launch collectors, which is overkill for simple previews.
// The remember { GameManager(...) } setup for previews remains a good static representation.

@Preview(showBackground = true)
@Composable
fun PreviewScoreView() {
    ScoreView(score = 12345)
}

@Preview(showBackground = true)
@Composable
fun PreviewNextPieceView() {
    val sampleTetromino = Tetromino(TetrominoShape.L, 0, Point(0,0), 2)
    NextPieceView(tetromino = sampleTetromino, cellSize = PreviewCellSize)
}

@Preview(showBackground = true)
@Composable
fun PreviewLevelView() {
    LevelView(level = 5)
}

@Preview(showBackground = true)
@Composable
fun PreviewGameControlsView() {
    GameControlsView({}, {}, {}, {}, {}, GameState.PLAYING)
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun PreviewGameScreen() {
    // Create a GameManager instance for the preview

    val previewGameManager = remember {
        val gm = GameManager(boardWidth = 10, boardHeight = 20)
        // For previews, we might need to manually set initial values for flows
        // or make startGame also update flows if it wasn't already.
        // (The refactored GameManager.startGame() should update flows)
        gm.startGame() // This should now correctly populate the flows for initial state.

        // To show specific pieces in preview, we'd ideally have a way to push to flows,
        // but for a static preview, direct modification of underlying state before flows are read
        // might be needed if initial flow values from startGame aren't what we want for a specific preview.
        // However, since `startGame` now calls `spawnNextTetromino` and `spawnNewTetromino`
        // which update flows, the preview should show some initial pieces.
        gm
    }

    MaterialTheme { // Wrap in MaterialTheme for previews to get typography
        GameScreen(gameManager = previewGameManager)
    }
}

// --- Level Start Animation ---

@Composable
fun LevelStartAnimationView(
    level: Int,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier // Modifier can be used to control if it fills the whole screen or just a part
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = level) { // Re-trigger if level changes while visible
        alpha.snapTo(0f) // Ensure alpha is 0 at the start of the effect for the new level
        // Fade In
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
        // Hold
        delay(1000L)
        // Fade Out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 500)
        )
        onAnimationComplete()
    }

    Box(
        // Default to fillMaxSize if no specific modifier is passed.
        // This ensures it overlays everything as per typical usage.
        modifier = modifier
            .background(TetrisColors.pausedBackground.copy(alpha = alpha.value * 0.85f)) // Slightly more opaque background
            .fillMaxSize(), // Make sure this is applied if modifier doesn't override it
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Level $level",
            style = MaterialTheme.typography.displayLarge, // Even larger text
            color = TetrisColors.textPrimary.copy(alpha = alpha.value),
            modifier = Modifier.alpha(alpha.value)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLevelStartAnimationView() {
    MaterialTheme {
        LevelStartAnimationView(level = 5, onAnimationComplete = {})
    }
}

// Add the LevelStartAnimationView to the GameScreen's main Box or as a general overlay
// For this integration, it will be added as an overlay covering the entire screen.
@Composable
fun GameScreenWithAllOverlays(gameManager: GameManager) {
    val currentTetromino by gameManager.currentTetrominoFlow.collectAsState()
    val nextTetromino by gameManager.nextTetrominoFlow.collectAsState()
    val gameState by gameManager.gameStateFlow.collectAsState()
    val score by gameManager.scoreFlow.collectAsState()
    val gameBoardState by gameManager.gameBoardFlow.collectAsState()
    val currentLevel by gameManager.currentLevelFlow.collectAsState()

    // Main game loop ticker
    LaunchedEffect(key1 = gameState, key2 = currentLevel) {
        // The loop should only run when the game is in the PLAYING state.
        // currentLevel is a key to restart the loop with a new delay if the level changes.
        if (gameState == GameState.PLAYING) {
            val tickDelay = gameManager.getTickDelayMillis()
            while (isActive) { // Use isActive from CoroutineScope
                gameManager.onGameTick()
                delay(tickDelay)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top section: Info panel - horizontal layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreView(score = score, modifier = Modifier.weight(1f))
            NextPieceView(
                tetromino = nextTetromino,
                cellSize = 12.dp, // Compact size for top bar
                modifier = Modifier.weight(1f)
            )
            LevelView(level = currentLevel, modifier = Modifier.weight(1f))
        }

        // Middle section: Game area - maximized
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Takes most of the screen
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Calculate optimal cell size based on available space
            val maxBoardWidth = 350.dp // Increased since no side panel
            val maxBoardHeight = 700.dp // Increased since top panel is compact
            val cellSizeByWidth = maxBoardWidth / 10 // 10 columns
            val cellSizeByHeight = maxBoardHeight / 20 // 20 rows
            val optimalCellSize = minOf(cellSizeByWidth, cellSizeByHeight, 30.dp) // Increased max size
            
            Box {
                GameBoardView(
                    gameBoard = gameBoardState.gameBoard, // Extract GameBoard from wrapper
                    cellSize = optimalCellSize
                )
                TetrominoView(
                    tetromino = currentTetromino,
                    cellSize = optimalCellSize
                )
            }

            // Overlays for game states
            if (gameState == GameState.GAME_OVER) {
                Box(
                    modifier = Modifier.fillMaxSize().background(TetrisColors.gameOverBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GAME OVER", style = MaterialTheme.typography.displayMedium, color = TetrisColors.textPrimary)
                }
            } else if (gameState == GameState.PAUSED && currentTetromino != null) {
                Box(
                    modifier = Modifier.fillMaxSize().background(TetrisColors.pausedBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PAUSED", style = MaterialTheme.typography.displayMedium, color = TetrisColors.textPrimary)
                }
            } else if (gameState == GameState.LEVEL_START_ANIMATING) {
                LevelStartAnimationView(
                    level = currentLevel,
                    onAnimationComplete = gameManager::completeLevelStartAnimation
                )
            }
        }

        // Bottom section: Control buttons
        GameControlsView(
            onMoveLeft = gameManager::moveLeft,
            onMoveRight = gameManager::moveRight,
            onRotate = gameManager::rotate,
            onDrop = gameManager::drop,
            onTogglePause = gameManager::togglePause,
            currentGameState = gameState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

// Update the original GameScreen to use the new structure with all overlays
// This replaces the previous GameScreen implementation.
@Composable
fun GameScreen(gameManager: GameManager) {
    GameScreenWithAllOverlays(gameManager = gameManager)
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun PreviewGameScreenGameOver() {
    val previewGameManager = remember {
        val gm = GameManager(boardWidth = 10, boardHeight = 20)
        gm.startGame() // Start it to initialize things

        // Simulate a game over state by directly setting the flow's value for preview purposes
        // This is a bit of a hack for previewing. In a real app, game logic sets this.
        val gameStateField = gm.javaClass.getDeclaredField("_gameStateFlow")
        gameStateField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val mutableGameStateFlow = gameStateField.get(gm) as MutableStateFlow<GameState>
        mutableGameStateFlow.value = GameState.GAME_OVER

        // Also clear current piece for game over
        val currentPieceField = gm.javaClass.getDeclaredField("_currentTetrominoFlow")
        currentPieceField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val mutableCurrentPieceFlow = currentPieceField.get(gm) as MutableStateFlow<Tetromino?>
        mutableCurrentPieceFlow.value = null

        // Add some score for preview
        val scoreField = gm.javaClass.getDeclaredField("_scoreFlow")
        scoreField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val mutableScoreFlow = scoreField.get(gm) as MutableStateFlow<Long>
        mutableScoreFlow.value = 1230L // Example score

        gm
    }
     MaterialTheme {
        GameScreen(gameManager = previewGameManager)
    }
}
