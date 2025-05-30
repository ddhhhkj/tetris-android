package com.example.tetris.gamecore

/**
 * Represents a 2D point or coordinate.
 * @param x The x-coordinate.
 * @param y The y-coordinate.
 */
data class Point(val x: Int, val y: Int)

/**
 * Enum representing the different shapes of Tetrominoes.
 * Each shape has a matrix defining its base rotation.
 * The matrix is a list of lists of integers, where 1 represents a block and 0 an empty space.
 */
enum class TetrominoShape(val matrix: List<List<Int>>) {
    I(listOf(
        listOf(0, 0, 0, 0),
        listOf(1, 1, 1, 1),
        listOf(0, 0, 0, 0),
        listOf(0, 0, 0, 0)
    )),
    O(listOf(
        listOf(1, 1),
        listOf(1, 1)
    )),
    T(listOf(
        listOf(0, 1, 0),
        listOf(1, 1, 1),
        listOf(0, 0, 0)
    )),
    S(listOf(
        listOf(0, 1, 1),
        listOf(1, 1, 0),
        listOf(0, 0, 0)
    )),
    Z(listOf(
        listOf(1, 1, 0),
        listOf(0, 1, 1),
        listOf(0, 0, 0)
    )),
    J(listOf(
        listOf(1, 0, 0),
        listOf(1, 1, 1),
        listOf(0, 0, 0)
    )),
    L(listOf(
        listOf(0, 0, 1),
        listOf(1, 1, 1),
        listOf(0, 0, 0)
    ));

    // Store all 4 rotations for each shape.
    val rotations: List<List<List<Int>>> by lazy {
        val allRotations = mutableListOf<List<List<Int>>>()
        var currentMatrix = this.matrix
        for (i in 0..3) {
            allRotations.add(currentMatrix)
            currentMatrix = rotateMatrixClockwise(currentMatrix)
        }
        allRotations
    }

    /**
     * The width of the Tetromino's bounding box for a specific rotation.
     */
    fun getWidth(rotation: Int): Int = rotations[rotation % 4].firstOrNull()?.size ?: 0

    /**
     * The height of the Tetromino's bounding box for a specific rotation.
     */
    fun getHeight(rotation: Int): Int = rotations[rotation % 4].size
}

/**
 * Rotates a given 2D matrix (list of lists) 90 degrees clockwise.
 * Assumes a square or rectangular matrix where all inner lists have the same size.
 * @param matrix The matrix to rotate.
 * @return A new matrix representing the rotated version.
 */
fun rotateMatrixClockwise(matrix: List<List<Int>>): List<List<Int>> {
    if (matrix.isEmpty() || matrix.first().isEmpty()) return matrix

    val rows = matrix.size
    val cols = matrix.first().size
    val newMatrix = Array(cols) { IntArray(rows) }

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            newMatrix[c][rows - 1 - r] = matrix[r][c]
        }
    }
    return newMatrix.map { it.toList() }
}

/**
 * Represents a single Tetromino game piece.
 * @param shape The type of Tetromino.
 * @param rotation The current rotation state (e.g., 0, 1, 2, 3 for 0, 90, 180, 270 degrees).
 * @param position The current top-left position of the Tetromino's bounding box on the game board.
 * @param color A placeholder for the color of the Tetromino.
 */
data class Tetromino(
    val shape: TetrominoShape,
    val rotation: Int, // 0, 1, 2, 3 representing 0, 90, 180, 270 degrees
    val position: Point,
    val color: Int // Placeholder for color, could be an actual color value or an ID
) {
    /**
     * Gets the actual 2D matrix for the current Tetromino based on its shape and rotation.
     */
    fun getCurrentMatrix(): List<List<Int>> {
        return shape.rotations[rotation % 4]
    }

    /**
     * Gets the width of the Tetromino's bounding box for its current rotation.
     */
    val width: Int get() = shape.getWidth(rotation)

    /**
     * Gets the height of the Tetromino's bounding box for its current rotation.
     */
    val height: Int get() = shape.getHeight(rotation)
}

/**
 * A map to store all base shapes and their pre-calculated rotations.
 * The key is the TetrominoShape, and the value is a list of its 4 rotated matrices.
 */
val shapeRotations: Map<TetrominoShape, List<List<List<Int>>>> = TetrominoShape.values().associate {
    it to it.rotations
}
