# Android Minesweeper

This repository contains the source code for a classic Minesweeper game developed for the Android platform as part of the CSCI-310 course. The application provides a complete game loop on a 10x10 grid, featuring a timer, gameplay modes, and a results screen.

## Features

*   **Classic Gameplay**: Familiar Minesweeper experience on a 10x10 grid with 5 randomly placed mines.
*   **Dual Modes**: Seamlessly switch between two modes:
    *   **Dig Mode (⛏️)**: Reveal cells to clear the board.
    *   **Flag Mode (🚩)**: Mark cells suspected of containing mines.
*   **Game Timer**: A timer starts on your first move and tracks your game duration.
*   **Recursive Reveal**: Clicking a cell with zero adjacent mines automatically reveals all neighboring empty cells.
*   **Game Over States**: The game concludes with a win by clearing all safe cells or a loss by revealing a mine.
*   **Results Screen**: After each game, a dedicated screen displays your win/loss status and the total time taken.
*   **Play Again**: A convenient button on the results screen allows you to start a new game immediately.

## How to Play

1.  The game board is a 10x10 grid of hidden cells.
2.  Tap the **⛏️ / 🚩** icon at the bottom of the screen to toggle between Dig and Flag modes.
3.  **In Dig Mode**: Tap a cell to reveal its content.
    *   If you reveal a mine (💣), you lose the game.
    *   If you reveal a number, it indicates how many mines are in the eight adjacent cells.
    *   If you reveal a blank cell, all adjacent safe cells will be uncovered automatically.
4.  **In Flag Mode**: Tap a cell to place a flag. Tap it again to remove the flag. You cannot dig a flagged cell.
5.  Win the game by revealing all cells on the board that are not mines.

## Getting Started

To build and run this project, you will need Android Studio.

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/mykechen/csci310_project1_minesweeper.git
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open" or "Open an Existing Project".
    *   Navigate to the cloned repository directory and click "OK".
3.  **Sync and Run:**
    *   Wait for Gradle to sync the project dependencies.
    *   Run the application on an Android emulator or a physical device.

## Key Files

*   `app/src/main/java/com/example/gridlayout/MainActivity.java`: The main activity containing the core game logic, including grid generation, user input handling, timer management, and win/loss conditions.
*   `app/src/main/java/com/example/gridlayout/ResultActivity.java`: The activity that displays the end-of-game summary, including the outcome and elapsed time.
*   `app/src/main/res/layout/activity_main.xml`: The XML layout file for the main game screen, defining the structure of the header, game info bar, `GridLayout`, and mode toggle button.
*   `app/src/main/res/layout/activity_result.xml`: The XML layout for the results screen.
*   `app/src/main/res/values/strings.xml`: Defines all string resources used in the app, including Unicode characters for the pickaxe, flag, mine, and clock icons.
