package com.example.tetris.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color // Keep for Preview if needed, but TetrisColors will be primary
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
// Import TetrisColors and the getComposeColor from the new Colors.kt
import com.example.tetris.gamecore.GameBoard
import com.example.tetris.gamecore.Point
import com.example.tetris.gamecore.Tetromino
import com.example.tetris.gamecore.TetrominoShape


/**
 * A Composable that draws a single square cell of the game board.
 *
 * @param color The background color of the cell.
 * @param size The size (width and height) of the cell.
 * @param modifier Modifier for customizing the cell.
 */
@Composable
fun CellView(
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .border(0.5.dp, TetrisColors.cellBorder) // Use TetrisColors for border
            .padding(0.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = color) // Background color is passed in, originates from getComposeColor
        }
    }
}

/**
 * A Composable that renders the entire game board.
 * It iterates through the gameBoard's state and draws each cell.
 *
 * @param gameBoard The GameBoard instance containing the state of the landed blocks.
 * @param cellSize The size of each individual cell on the board.
 */
@Composable
fun GameBoardView(
    gameBoard: GameBoard,
    cellSize: Dp
) {
    Column {
        for (y in 0 until gameBoard.height) {
            Row {
                for (x in 0 until gameBoard.width) {
                    val cellValue = gameBoard.getCell(x, y)
                    CellView(
                        color = getComposeColor(cellValue),
                        size = cellSize
                    )
                }
            }
        }
    }
}

/**
 * A Composable that renders the currently falling Tetromino.
 * It draws the Tetromino's blocks at their current position on the board.
 * This Composable is intended to be overlaid on top of the GameBoardView.
 *
 * @param tetromino The current Tetromino to display. Can be null.
 * @param cellSize The size of each individual block of the Tetromino.
 */
@Composable
fun TetrominoView(
    tetromino: Tetromino?,
    cellSize: Dp
) {
    tetromino?.let { piece ->
        val matrix = piece.getCurrentMatrix()
        val (offsetX, offsetY) = with(LocalDensity.current) {
            Pair(piece.position.x * cellSize.toPx(), piece.position.y * cellSize.toPx())
        }

        // This Box acts as a container for the Tetromino, positioned according to the Tetromino's top-left.
        // The individual cells are then drawn relative to this Box.
        Box(modifier = Modifier.offset(x = (piece.position.x * cellSize.value).dp, y = (piece.position.y * cellSize.value).dp)) {
            matrix.forEachIndexed { y_matrix, row ->
                row.forEachIndexed { x_matrix, cellValue ->
                    if (cellValue != 0) {
                        CellView(
                            color = getComposeColor(piece.color),
                            size = cellSize,
                            // Position each block of the Tetromino relative to the TetrominoView's container
                            modifier = Modifier.offset(
                                x = (x_matrix * cellSize.value).dp,
                                y = (y_matrix * cellSize.value).dp
                            )
                        )
                    }
                }
            }
        }
    }
}


// --- Preview Functions ---

@Preview(showBackground = true)
@Composable
fun PreviewCellView() {
    CellView(color = Color.Red, size = 30.dp)
}

@Preview(showBackground = true)
@Composable
fun PreviewGameBoardView() {
    val board = GameBoard(width = 10, height = 20)
    // Manually set some sample blocks for preview by placing tetrominoes and clearing lines
    val sampleTetromino1 = Tetromino(TetrominoShape.L, 0, Point(3, 17), 1)
    val sampleTetromino2 = Tetromino(TetrominoShape.O, 0, Point(6, 15), 2)
    board.placeTetrominoAndGetLinesCleared(sampleTetromino1)
    board.placeTetrominoAndGetLinesCleared(sampleTetromino2)
    GameBoardView(gameBoard = board, cellSize = 20.dp)
}

@Preview(showBackground = true)
@Composable
fun PreviewTetrominoView() {
    // Create a dummy Tetromino for previewing
    val sampleTetromino = Tetromino(
        shape = TetrominoShape.T,
        rotation = 0,
        position = Point(0, 0), // Positioned at top-left of its container for preview simplicity
        color = 3 // Green
    )
    // Wrap in a Box to give it some space and a background for visibility
    Box(modifier = Modifier.size(120.dp).padding(4.dp)) {
        TetrominoView(tetromino = sampleTetromino, cellSize = 20.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameBoardWithTetromino() {
    val board = GameBoard(width = 5, height = 10) // Smaller board for preview
    val landedPiece = Tetromino(TetrominoShape.S, 0, Point(1, 7), 4)
    board.placeTetrominoAndGetLinesCleared(landedPiece) // Landed piece

    val currentPiece = Tetromino(TetrominoShape.J, 1, Point(1, 1), 5)

    Box(modifier = Modifier.size( (5 * 20).dp, (10*20).dp)) { // Rough size of board
        GameBoardView(gameBoard = board, cellSize = 20.dp)
        TetrominoView(tetromino = currentPiece, cellSize = 20.dp)
    }
}
