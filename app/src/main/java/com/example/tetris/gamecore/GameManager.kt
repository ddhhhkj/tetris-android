package com.example.tetris.gamecore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

/**
 * Represents the different states of the game.
 */
enum class GameState {
    PLAYING,
    PAUSED,
    LEVEL_START_ANIMATING, // New state for level start animations
    GAME_OVER
}

/**
 * Wrapper class for GameBoard to enable StateFlow updates when board content changes
 */
data class GameBoardState(
    val gameBoard: GameBoard,
    val updateId: Long = 0L
)

/**
 * Manages the overall game logic, state, and interactions using StateFlow for UI reactivity.
 * @param boardWidth The width of the game board.
 * @param boardHeight The height of the game board.
 */
class GameManager(private val boardWidth: Int, private val boardHeight: Int) {

    private val gameBoard: GameBoard = GameBoard(boardWidth, boardHeight)
    
    // Board update counter to force StateFlow emissions
    private var boardUpdateCounter = 0L

    // --- StateFlows for UI observation ---
    private val _currentTetrominoFlow = MutableStateFlow<Tetromino?>(null)
    val currentTetrominoFlow: StateFlow<Tetromino?> = _currentTetrominoFlow

    private val _nextTetrominoFlow = MutableStateFlow<Tetromino?>(null)
    val nextTetrominoFlow: StateFlow<Tetromino?> = _nextTetrominoFlow

    private val _gameStateFlow = MutableStateFlow(GameState.PAUSED) // Initial state
    val gameStateFlow: StateFlow<GameState> = _gameStateFlow

    private val _scoreFlow = MutableStateFlow(0L)
    val scoreFlow: StateFlow<Long> = _scoreFlow

    private val _gameBoardFlow = MutableStateFlow(GameBoardState(gameBoard, 0L))
    val gameBoardFlow: StateFlow<GameBoardState> = _gameBoardFlow

    private val _currentLevelFlow = MutableStateFlow(1)
    val currentLevelFlow: StateFlow<Int> = _currentLevelFlow

    // Internal state for level progression
    private var linesClearedThisLevel: Int = 0
    private val linesPerLevel: Int = 10 // Lines needed to advance to the next level
    private val maxLevel: Int = 20 // Maximum game level

    // Game Speed
    private val baseTickDelayMillis = 1000L
    private val minTickDelayMillis = 75L // Minimum delay, prevents game from becoming too fast
    private val speedFactorPerLevel = 0.80 // Game speed increases by 20% each level (delay * 0.80)


    init {
        // Ensure initial state is consistent
        reset()
    }

    /**
     * Starts a new game.
     * Resets the board, score, level, and spawns initial Tetrominoes.
     * Updates all relevant StateFlows.
     */
    fun startGame() {
        reset() // Reset all game states including level and lines cleared

        _gameStateFlow.value = GameState.PLAYING
        spawnNextTetromino() // Initialize nextTetrominoFlow
        spawnNewTetromino()  // currentTetrominoFlow becomes next, new next is generated
    }

    /**
     * Spawns a new Tetromino to be the `currentTetromino`.
     * The previous `nextTetromino` becomes the `currentTetromino`.
     * A new random `nextTetromino` is generated.
     * Checks for game over condition if the new `currentTetromino` cannot be placed.
     * Updates StateFlows.
     */
    private fun spawnNewTetromino() {
        _currentTetrominoFlow.value = _nextTetrominoFlow.value
        spawnNextTetromino() // Generate the new "next" piece for _nextTetrominoFlow

        _currentTetrominoFlow.value?.let {
            // Adjust spawn position if needed, e.g., center it
            val spawnX = (boardWidth - it.width) / 2
            val spawnY = 0 // Typically spawn at the top
            val positionedTetromino = it.copy(position = Point(spawnX, spawnY))
            _currentTetrominoFlow.value = positionedTetromino

            if (!gameBoard.canMove(positionedTetromino, 0, 0)) {
                _gameStateFlow.value = GameState.GAME_OVER
                _currentTetrominoFlow.value = null // Can't place the new piece
            }
        }
        // Ensure game board is re-emitted if game over state changes how it might be displayed (e.g. final piece)
        forceGameBoardUpdate()
    }

    /**
     * Generates a new random Tetromino and assigns it to `_nextTetrominoFlow`.
     */
    private fun spawnNextTetromino() {
        val randomShape = TetrominoShape.values().random(Random)
        // Assign color based on TetrominoShape.ordinal + 1 for consistent mapping
        // This ensures I=1, O=2, T=3, S=4, Z=5, J=6, L=7 in getComposeColor
        val shapeColor = randomShape.ordinal + 1
        // Spawn at a temporary position, will be adjusted when it becomes current
        _nextTetrominoFlow.value = Tetromino(shape = randomShape, rotation = 0, position = Point(0,0), color = shapeColor)
    }

    /**
     * Forces an update to the GameBoard StateFlow by incrementing the update counter.
     * This is necessary because StateFlow only emits when the reference changes,
     * but we modify the GameBoard's internal state.
     */
    private fun forceGameBoardUpdate() {
        boardUpdateCounter++
        // Create a deep copy to force StateFlow emission
        val boardCopy = gameBoard.deepCopy()
        _gameBoardFlow.value = GameBoardState(boardCopy, boardUpdateCounter)
    }

    /**
     * Handles the logic for a single game tick (e.g., called by a timer).
     * Attempts to move the current Tetromino down. If it lands, places it
     * and spawns a new one. Updates StateFlows.
     */
    fun onGameTick() {
        if (_gameStateFlow.value != GameState.PLAYING || _currentTetrominoFlow.value == null) {
            return
        }

        val currentPiece = _currentTetrominoFlow.value!!
        val movedDown = gameBoard.moveDown(currentPiece)

        if (movedDown != null) {
            _currentTetrominoFlow.value = movedDown
        } else {
            // Tetromino has landed
            val linesCleared = gameBoard.placeTetrominoAndGetLinesCleared(currentPiece)
            _scoreFlow.value = gameBoard.score    // Update score flow
            
            // Force StateFlow update after board modification
            forceGameBoardUpdate()

            if (linesCleared > 0) {
                updateLevelProgress(linesCleared)
            }

            spawnNewTetromino() // This will set _gameStateFlow to GAME_OVER if needed
                                // and update _currentTetrominoFlow, _nextTetrominoFlow

            if (_gameStateFlow.value == GameState.GAME_OVER) {
                // Game over - could add additional handling here if needed
            }
        }
    }

    /**
     * Updates the level progress based on the number of lines cleared.
     * @param newlyClearedLines The number of lines cleared in the last move.
     */
    private fun updateLevelProgress(newlyClearedLines: Int) {
        if (_currentLevelFlow.value >= maxLevel) {
            // Already at max level, can still accumulate lines for score but level doesn't change
            linesClearedThisLevel += newlyClearedLines // Optional: track lines even at max level for other purposes
            return
        }

        linesClearedThisLevel += newlyClearedLines
        if (linesClearedThisLevel >= linesPerLevel) {
            val currentLevel = _currentLevelFlow.value
            if (currentLevel < maxLevel) {
                _currentLevelFlow.value = currentLevel + 1
                linesClearedThisLevel %= linesPerLevel // Or linesClearedThisLevel -= linesPerLevel
                _gameStateFlow.value = GameState.LEVEL_START_ANIMATING // Trigger animation state
                println("Level Up! New Level: ${_currentLevelFlow.value}. Animating...")
            } else {
                // Cap level at maxLevel, ensure it doesn't exceed
                _currentLevelFlow.value = maxLevel
                // If we hit max level, we don't go into LEVEL_START_ANIMATING unless specifically designed.
                // For now, assume normal play continues or game ends if max level means game completion.
                // Current logic: just stays in PLAYING or proceeds to GAME_OVER via spawnNewTetromino.
                // linesClearedThisLevel can be reset or just allowed to accumulate beyond linesPerLevel
                // if no further level progression is possible.
                linesClearedThisLevel = 0 // Reset for clarity or if some bonus depends on it
            }
        }
    }

    /**
     * Calculates the delay for the game tick based on the current level.
     * @return The tick delay in milliseconds.
     */
    fun getTickDelayMillis(): Long {
        val level = _currentLevelFlow.value
        // Formula: baseDelay * (factor ^ (level - 1))
        // Using Math.pow which returns Double, then convert to Long
        var delay = baseTickDelayMillis
        for (i in 1 until level) {
            delay = (delay * speedFactorPerLevel).toLong()
        }
        return maxOf(minTickDelayMillis, delay)
    }


    // --- Input Handling Stubs ---

    /**
     * Attempts to move the current Tetromino to the left. Updates StateFlow.
     */
    fun moveLeft() {
        if (_gameStateFlow.value == GameState.PLAYING && _currentTetrominoFlow.value != null) {
            val moved = gameBoard.moveLeft(_currentTetrominoFlow.value!!)
            if (moved != null) {
                _currentTetrominoFlow.value = moved
            }
        }
    }

    /**
     * Attempts to move the current Tetromino to the right. Updates StateFlow.
     */
    fun moveRight() {
        if (_gameStateFlow.value == GameState.PLAYING && _currentTetrominoFlow.value != null) {
            val moved = gameBoard.moveRight(_currentTetrominoFlow.value!!)
            if (moved != null) {
                _currentTetrominoFlow.value = moved
            }
        }
    }

    /**
     * Attempts to rotate the current Tetromino. Updates StateFlow.
     */
    fun rotate() {
        if (_gameStateFlow.value == GameState.PLAYING && _currentTetrominoFlow.value != null) {
            val rotated = gameBoard.rotate(_currentTetrominoFlow.value!!)
            _currentTetrominoFlow.value = rotated
        }
    }

    /**
     * Drops the current Tetromino to the lowest possible position instantly. Updates StateFlows.
     */
    fun drop() {
        if (_gameStateFlow.value == GameState.PLAYING && _currentTetrominoFlow.value != null) {
            var tempTetromino = _currentTetrominoFlow.value!!
            while (true) {
                val movedDown = gameBoard.moveDown(tempTetromino)
                if (movedDown != null) {
                    tempTetromino = movedDown
                } else {
                    break // Cannot move down further
                }
            }
            _currentTetrominoFlow.value = tempTetromino // Update currentTetromino to its final pre-landing position
            onGameTick() // Force one more tick to place it, update board/score, and spawn next
        }
    }

    /**
     * Toggles the game state between PLAYING and PAUSED. Updates StateFlow.
     * Does nothing if the game is GAME_OVER.
     */
    fun togglePause() {
        _gameStateFlow.value = when (_gameStateFlow.value) {
            GameState.PLAYING -> GameState.PAUSED
            GameState.PAUSED -> GameState.PLAYING
            GameState.LEVEL_START_ANIMATING -> GameState.PAUSED // Pause during animation
            GameState.GAME_OVER -> GameState.GAME_OVER // Cannot unpause if game is over
        }
    }

    /**
     * Resets the game to its initial state, ready for a new game to be started.
     * Updates all relevant StateFlows.
     */
    fun reset() {
        gameBoard.clearBoard() // Resets score internally too
        _currentTetrominoFlow.value = null
        _nextTetrominoFlow.value = null
        _scoreFlow.value = gameBoard.score // Should be 0 after clearBoard
        forceGameBoardUpdate() // Emit cleared board
        _currentLevelFlow.value = 1
        linesClearedThisLevel = 0
        _gameStateFlow.value = GameState.PAUSED // Or GAME_OVER, depending on desired initial screen
    }

    /**
     * Call this function when the level start animation/transition is complete.
     * It sets the game state back to PLAYING.
     */
    fun completeLevelStartAnimation() {
        if (_gameStateFlow.value == GameState.LEVEL_START_ANIMATING) {
            _gameStateFlow.value = GameState.PLAYING
            println("Level start animation complete. Resuming play.")
        }
    }
}
