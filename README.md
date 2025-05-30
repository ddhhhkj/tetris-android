# Tetris Android Game

A modern Tetris game built with Kotlin and Jetpack Compose for Android.

## Download APK

**[📱 Download tetris-android-v1.0.apk](./tetris-android-v1.0.apk)** (9.6MB)

Ready to play! Just download and install on your Android device.

## Features

- **Classic Tetris Gameplay**: All 7 standard Tetromino pieces (I, O, T, S, Z, J, L)
- **Modern UI**: Built with Jetpack Compose for smooth animations and responsive design
- **Level Progression**: Automatic level advancement with increasing speed
- **Score System**: Points awarded for line clearing (40, 100, 300, 1200 for 1-4 lines)
- **Responsive Layout**: Optimized for mobile devices with compact controls
- **Game States**: Play, Pause, Game Over, and Level Start animations

## Game Controls

- **←** : Move piece left
- **→** : Move piece right  
- **↻** : Rotate piece
- **Drop** : Instantly drop piece to bottom
- **Pause/Play** : Toggle game pause

## Technical Architecture

### Core Components

- **GameManager**: Manages game state, logic, and StateFlow emissions
- **GameBoard**: Handles board state, piece placement, and line clearing
- **Tetromino**: Represents game pieces with rotation and positioning
- **UI Components**: Compose-based UI with reactive state management

### Key Features

- **StateFlow Integration**: Reactive UI updates using Kotlin StateFlow
- **Deep Copy Pattern**: Ensures proper state updates for UI reactivity
- **Modular Design**: Separated game logic from UI presentation
- **Debug Support**: Comprehensive logging for development

## Project Structure

```
app/src/main/java/com/example/tetris/
├── gamecore/
│   ├── GameManager.kt      # Main game logic controller
│   ├── GameBoard.kt        # Board state and operations
│   ├── Tetromino.kt        # Piece definitions and rotations
│   └── TetrominoShape.kt   # Shape enumerations
├── ui/
│   ├── GameScreenComposables.kt  # Main game UI
│   ├── BoardComposables.kt       # Board rendering
│   ├── Colors.kt                 # Color definitions
│   └── TetrisColors.kt          # Theme colors
└── MainActivity.kt         # App entry point
```

## Build Requirements

- Android Studio Arctic Fox or later
- Kotlin 1.9.0+
- Android SDK 35
- Gradle 8.7+

## Installation

### Option 1: Download APK (Recommended)
1. Download the APK file from the link above
2. Enable "Install from Unknown Sources" in your Android settings
3. Install the APK on your device
4. Enjoy playing!

### Option 2: Build from Source
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on device or emulator

## Game Rules

- Pieces fall from the top of the board
- Rotate and position pieces to form complete horizontal lines
- Complete lines are cleared and award points
- Game ends when pieces reach the top
- Speed increases with each level (every 10 lines cleared)

## Development Notes

This project demonstrates modern Android development practices:
- Jetpack Compose for UI
- StateFlow for reactive programming
- MVVM-like architecture with GameManager
- Kotlin coroutines for game timing
- Material Design 3 theming

## License

This project is open source and available under the MIT License.
