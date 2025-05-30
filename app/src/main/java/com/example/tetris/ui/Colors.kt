package com.example.tetris.ui

import androidx.compose.ui.graphics.Color

/**
 * Defines the color palette for the Tetris game.
 */
object TetrisColors {
    // Standard Tetris colors
    val I_Shape = Color(0xFF00FFFF) // Cyan
    val O_Shape = Color(0xFFFFFF00) // Yellow
    val T_Shape = Color(0xFF800080) // Purple
    val S_Shape = Color(0xFF00FF00) // Green
    val Z_Shape = Color(0xFFFF0000) // Red
    val J_Shape = Color(0xFF0000FF) // Blue
    val L_Shape = Color(0xFFFFA500) // Orange

    // Board and UI element colors
    val boardBackground = Color(0xFF202020) // Darker gray for the board
    val cellBorder = Color(0xFF404040)      // Lighter gray for borders
    val emptyCell = Color(0xFF303030)       // Slightly lighter than board for empty cells

    // Overlay backgrounds
    val gameOverBackground = Color(0xAA000000) // Semi-transparent black
    val pausedBackground = Color(0xAA000000)   // Semi-transparent black

    // Text colors (optional, can use MaterialTheme defaults)
    val textPrimary = Color.White
    val textSecondary = Color.LightGray
}

/**
 * Maps an integer block value to a specific Tetromino or board color.
 * This function assumes that the `blockValue` passed from `Tetromino.color` or `GameBoard.getCell()`
 * is designed to correspond to these fixed colors.
 *
 * 0: Empty cell on the board.
 * 1-7: Correspond to Tetromino shapes I, O, T, S, Z, J, L respectively.
 *
 * @param blockValue The integer value representing the block type or color.
 * @return The Jetpack Compose Color.
 */
fun getComposeColor(blockValue: Int): Color {
    return when (blockValue) {
        0 -> TetrisColors.emptyCell // Empty cell on the board
        1 -> TetrisColors.I_Shape
        2 -> TetrisColors.O_Shape
        3 -> TetrisColors.T_Shape
        4 -> TetrisColors.S_Shape
        5 -> TetrisColors.Z_Shape
        6 -> TetrisColors.J_Shape
        7 -> TetrisColors.L_Shape
        else -> TetrisColors.boardBackground // Fallback or default board color if value is unexpected
    }
}
