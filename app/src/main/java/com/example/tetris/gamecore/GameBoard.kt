package com.example.tetris.gamecore

/**
 * Represents the game board.
 * @param width The width of the board in cells.
 * @param height The height of the board in cells.
 */
class GameBoard(val width: Int, val height: Int) {
    // The board is represented by a 2D array. 0 means empty, other integers can represent block colors.
    private val board: Array<IntArray> = Array(height) { IntArray(width) }
    var score: Long = 0L
        private set // Score can only be modified internally by GameBoard

    /**
     * Initializes the board, setting all cells to 0 (empty).
     */
    init {
        clearBoard()
    }

    /**
     * Resets the board and score.
     */
    fun clearBoard() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                board[y][x] = 0
            }
        }
        score = 0L
    }

    /**
     * Gets the value of a cell at the given coordinates.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The value of the cell, or -1 if out of bounds.
     */
    fun getCell(x: Int, y: Int): Int {
        return if (x in 0 until width && y in 0 until height) {
            board[y][x]
        } else {
            -1 // Indicate out of bounds
        }
    }

    /**
     * Checks if a given Tetromino can move by dx, dy without collision or going out of bounds.
     * @param tetromino The Tetromino to check.
     * @param dx The change in x-coordinate.
     * @param dy The change in y-coordinate.
     * @return True if the move is valid, false otherwise.
     */
    fun canMove(tetromino: Tetromino, dx: Int, dy: Int): Boolean {
        val matrix = tetromino.getCurrentMatrix()
        val newPosition = Point(tetromino.position.x + dx, tetromino.position.y + dy)

        for (y in matrix.indices) {
            for (x in matrix[y].indices) {
                if (matrix[y][x] != 0) { // If it's a block of the Tetromino
                    val boardX = newPosition.x + x
                    val boardY = newPosition.y + y

                    // Check bounds
                    if (boardX < 0 || boardX >= width || boardY < 0 || boardY >= height) {
                        return false // Out of bounds
                    }

                    // Check collision with existing blocks on the board
                    if (board[boardY][boardX] != 0) {
                        return false // Collision
                    }
                }
            }
        }
        return true
    }

    /**
     * Attempts to move the Tetromino to the left.
     * @param currentTetromino The current Tetromino.
     * @return A new Tetromino instance at the new position if successful, null otherwise.
     */
    fun moveLeft(currentTetromino: Tetromino): Tetromino? {
        return if (canMove(currentTetromino, -1, 0)) {
            currentTetromino.copy(position = currentTetromino.position.copy(x = currentTetromino.position.x - 1))
        } else {
            null
        }
    }

    /**
     * Attempts to move the Tetromino to the right.
     * @param currentTetromino The current Tetromino.
     * @return A new Tetromino instance at the new position if successful, null otherwise.
     */
    fun moveRight(currentTetromino: Tetromino): Tetromino? {
        return if (canMove(currentTetromino, 1, 0)) {
            currentTetromino.copy(position = currentTetromino.position.copy(x = currentTetromino.position.x + 1))
        } else {
            null
        }
    }

    /**
     * Attempts to move the Tetromino down.
     * @param currentTetromino The current Tetromino.
     * @return A new Tetromino instance at the new position if successful, null otherwise.
     */
    fun moveDown(currentTetromino: Tetromino): Tetromino? {
        return if (canMove(currentTetromino, 0, 1)) {
            currentTetromino.copy(position = currentTetromino.position.copy(y = currentTetromino.position.y + 1))
        } else {
            null
        }
    }

    /**
     * Places the blocks of the Tetromino onto the game board, clears lines,
     * updates the score, and returns the number of lines cleared.
     * This is typically called when the Tetromino can no longer move down.
     * @param tetromino The Tetromino to place.
     * @return The number of lines cleared.
     */
    fun placeTetrominoAndGetLinesCleared(tetromino: Tetromino): Int {
        val matrix = tetromino.getCurrentMatrix()
        
        for (y_matrix in matrix.indices) {
            for (x_matrix in matrix[y_matrix].indices) {
                if (matrix[y_matrix][x_matrix] != 0) {
                    val boardX = tetromino.position.x + x_matrix
                    val boardY = tetromino.position.y + y_matrix
                    // Ensure placement is within bounds
                    if (boardX in 0 until width && boardY in 0 until height) {
                        board[boardY][boardX] = tetromino.color // Use Tetromino's color
                    }
                }
            }
        }
        
        // After placing, clear lines and update score
        val linesCleared = clearLines()
        updateScore(linesCleared) // Score is updated internally in GameBoard
        
        return linesCleared
    }

    /**
     * Checks for and clears any filled lines on the board.
     * Shifts rows down to fill cleared lines.
     * @return The number of lines cleared.
     */
    private fun clearLines(): Int {
        var linesCleared = 0
        var currentRow = height - 1 // Start checking from the bottom row

        while (currentRow >= 0) {
            if (isLineFull(currentRow)) {
                linesCleared++
                // Remove the line and shift rows above it down
                for (yToShift in currentRow downTo 1) {
                    for (x in 0 until width) {
                        board[yToShift][x] = board[yToShift - 1][x]
                    }
                }
                // Clear the top-most line (it's now empty)
                for (x in 0 until width) {
                    board[0][x] = 0
                }
                // Since rows shifted down, re-check the current row index
                // (which now contains the row previously above it)
                // No need to decrement currentRow here, as it will be checked again.
            } else {
                currentRow-- // Move to check the next row up
            }
        }
        return linesCleared
    }

    /**
     * Checks if a specific row is completely filled with blocks.
     * @param rowIndex The index of the row to check.
     * @return True if the line is full, false otherwise.
     */
    private fun isLineFull(rowIndex: Int): Boolean {
        if (rowIndex < 0 || rowIndex >= height) return false
        for (x in 0 until width) {
            if (board[rowIndex][x] == 0) { // 0 represents an empty cell
                return false
            }
        }
        return true
    }

    /**
     * Updates the score based on the number of lines cleared.
     * @param linesCleared The number of lines cleared in the last move.
     */
    private fun updateScore(linesCleared: Int) {
        when (linesCleared) {
            1 -> score += 40
            2 -> score += 100
            3 -> score += 300
            4 -> score += 1200
            // No points for 0 lines
        }
        // Could add level-based multipliers here in the future
    }


    /**
     * Placeholder for Tetromino rotation logic.
     * The actual implementation is deferred.
     * @param tetromino The Tetromino to rotate.
     * @return A new Tetromino instance with the new rotation, or null if rotation is not possible.
     */
    fun rotate(tetromino: Tetromino): Tetromino {
        val newRotation = (tetromino.rotation + 1) % 4
        val rotatedTetromino = tetromino.copy(rotation = newRotation)

        // Check if the rotated piece is valid at its current position.
        // canMove is used here with dx=0, dy=0 to check the new shape in the same spot.
        if (canMove(rotatedTetromino, 0, 0)) {
            return rotatedTetromino
        }

        // Basic wall kick: Try moving 1 unit left or right if rotation failed at current spot
        // More sophisticated wall kicks (like SRS) would involve more complex offset checks.

        // Try moving 1 right
        if (canMove(rotatedTetromino, 1, 0)) {
            return rotatedTetromino.copy(position = rotatedTetromino.position.copy(x = rotatedTetromino.position.x + 1))
        }
        // Try moving 1 left
        if (canMove(rotatedTetromino, -1, 0)) {
            return rotatedTetromino.copy(position = rotatedTetromino.position.copy(x = rotatedTetromino.position.x - 1))
        }
        // Try moving 1 down (less common for wall kicks, but can be useful for some shapes)
         if (canMove(rotatedTetromino, 0, 1)) {
            return rotatedTetromino.copy(position = rotatedTetromino.position.copy(y = rotatedTetromino.position.y + 1))
        }


        // If rotation and basic kicks fail, return the original tetromino
        return tetromino
    }

    /**
     * Creates a deep copy of this GameBoard.
     * This is needed for StateFlow to detect changes.
     */
    fun deepCopy(): GameBoard {
        val newBoard = GameBoard(width, height)
        // Copy the board array
        for (y in 0 until height) {
            for (x in 0 until width) {
                newBoard.board[y][x] = this.board[y][x]
            }
        }
        // Copy the score
        newBoard.score = this.score
        return newBoard
    }

    /**
     * Utility function to print the board to the console for debugging.
     */
    fun printBoard() {
        for (y in 0 until height) {
            for (x in 0 until width) {
                print("${board[y][x]} ")
            }
            println()
        }
    }
}
