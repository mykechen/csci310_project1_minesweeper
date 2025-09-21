package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 10;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    // Timer variables
    private TextView timerDisplay;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime;
    private boolean isTimerRunning = false;

    // Mode variables
    private TextView modeButton;
    private boolean isPickaxeMode = true;

    // Minefield variables
    private static final int MINE_COUNT = 5;
    private boolean[][] minefield;
    private boolean[][] revealed;
    private boolean gameStarted = false;
    private boolean gameOver = false;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cell_tvs = new ArrayList<TextView>();

        // Initialize timer components
        initializeTimer();

        // Initialize mode toggle
        toggleMode();

        // Initialize minefield
        initializeMineField();

        // Dynamically create the cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                TextView tv = new TextView(this);
                tv.setHeight(dpToPixel(32));
                tv.setWidth(dpToPixel(32));
                tv.setTextSize(16);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.parseColor("#88E788")); // Starting color
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(1), dpToPixel(1), dpToPixel(1), dpToPixel(1));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }
    }

    private void initializeTimer() {
        timerDisplay = findViewById(R.id.timer_display);

        timerHandler = new Handler();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    int seconds = (int) (elapsedTime / 1000);

                    // Format time as just the number (no leading zeros)
                    String timeText = String.valueOf(seconds);
                    timerDisplay.setText(timeText);

                    // Update every second for cleaner updates
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startTimer() {
        if (!isTimerRunning) {
            System.out.println("Starting timer...");
            isTimerRunning = true;
            startTime = System.currentTimeMillis();
            timerHandler.post(timerRunnable);
        }
    }

    private void stopTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        stopTimer();
        timerDisplay.setText("0");
    }

    private void toggleMode() {
        modeButton = findViewById(R.id.mode_button);

        if (modeButton == null) {
            System.out.println("ERROR: mode_button not found!");
            return;
        }

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle mode and update icon in one function
                isPickaxeMode = !isPickaxeMode;

                if (isPickaxeMode) {
                    modeButton.setText(getString(R.string.pick)); // switch to pickaxe
                    System.out.println("Switched to pickaxe mode");
                } else {
                    modeButton.setText(getString(R.string.flag)); // switch to flag
                    System.out.println("Switched to flag mode");
                }
            }
        });
    }

    private void initializeMineField() {
        // Initialize minefield and revealed arrays
        minefield = new boolean[COLUMN_COUNT][COLUMN_COUNT];
        revealed = new boolean[COLUMN_COUNT][COLUMN_COUNT];

        // Clear both arrays
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                minefield[i][j] = false;
                revealed[i][j] = false;
            }
        }

        // Place mines randomly
        java.util.Random random = new java.util.Random();
        int minesPlaced = 0;

        while (minesPlaced < MINE_COUNT) {
            int row = random.nextInt(10);
            int col = random.nextInt(10);

            // Check if this position doesn't already have a mine
            if (!minefield[row][col]) {
                minefield[row][col] = true;
                minesPlaced++;
                System.out.println("Mine placed at: (" + row + ", " + col + ")");
            }
        }

        System.out.println("Total mines placed: " + minesPlaced);
        printMineLocations(); // Debug output
    }

    private void printMineLocations() {
        System.out.println("=== MINE FIELD DEBUG ===");
        for (int i = 0; i < 10; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                row.append(minefield[i][j] ? "* " : ". ");
            }
            System.out.println("Row " + i + ": " + row.toString());
        }
        System.out.println("=======================");
    }

    private void resetGrid() {
        // Reset all cell appearances to starting state
        for (TextView cell : cell_tvs) {
            cell.setText("");
            cell.setTextColor(Color.BLACK);
            cell.setBackgroundColor(Color.parseColor("#88E788")); // Starting color
        }
    }

    private boolean isMine(int row, int col) {
        if (row < 0 || row >= 10 || col < 0 || col >= 10) {
            return false;
        }
        return minefield[row][col];
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n = 0; n < cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view) {
        try {
            // check if game over - if so, don't process any more moves
            if (gameOver) {
                System.out.println("Game is over, ignoring click");
                return;
            }

            // start timer on first click
            if (!isTimerRunning) {
                System.out.println("First click detected - starting timer");
                startTimer();
                gameStarted = true;
            }

            TextView tv = (TextView) view;
            int n = findIndexOfCellTextView(tv);

            // Validate cell index
            if (n < 0 || n >= cell_tvs.size()) {
                System.out.println("ERROR: Invalid cell index: " + n);
                return;
            }

            int i = n / COLUMN_COUNT;
            int j = n % COLUMN_COUNT;

            // Validate coordinates
            if (i < 0 || i >= 10 || j < 0 || j >= 10) {
                System.out.println("ERROR: Invalid coordinates: (" + i + ", " + j + ")");
                return;
            }

            System.out.println("Clicked cell at (" + i + ", " + j + "), mode: " + (isPickaxeMode ? "dig" : "flag"));

            if (isPickaxeMode) {
                // Digging mode

                // Can't dig flagged cells
                String currentText = tv.getText().toString();
                String flagText = getString(R.string.flag);
                if (currentText.equals(flagText)) {
                    System.out.println("Cannot dig flagged cell at (" + i + ", " + j + ")");
                    return;
                }

                // Can't dig already revealed cells
                if (revealed[i][j]) {
                    System.out.println("Cell (" + i + ", " + j + ") already revealed");
                    return;
                }

                // Mark as revealed
                revealed[i][j] = true;

                if (isMine(i, j)) {
                    // Hit a mine - game over (LOSE)
                    System.out.println("BOOM! Hit mine at (" + i + ", " + j + ")");

                    tv.setText(getString(R.string.mine));
                    tv.setTextColor(Color.RED);
                    tv.setBackgroundColor(Color.parseColor("#ffcccb")); // Light coral

                    // End game first
                    endGame(false); // false = lost

                    // Start sequential mine reveal animation
                    revealAllMinesSequentially();

                } else {
                    // Safe cell - show adjacent mine count
                    int adjacentMines = countAdjacentMines(i, j);
                    tv.setText(adjacentMines == 0 ? "" : String.valueOf(adjacentMines));
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.parseColor("#E0E0E0")); // Clicked color
                    System.out.println("Safe cell (" + i + ", " + j + ") - Adjacent mines: " + adjacentMines);

                    // Check for win condition
                    if (checkWinCondition()) {
                        System.out.println("WIN CONDITION MET!");
                        endGame(true); // true = won

                        // For wins, go directly to results (no mine reveal needed)
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showResults();
                            }
                        }, 1000); // 1 second delay for win
                    }
                }
            } else {
                // Flagging mode

                // Can't flag an already revealed cell
                if (revealed[i][j]) {
                    System.out.println("Cannot flag revealed cell at (" + i + ", " + j + ")");
                    return;
                }

                String currentText = tv.getText().toString();
                String flagText = getString(R.string.flag);

                // Toggle flag
                if (currentText.equals(flagText)) {
                    // Remove flag
                    tv.setText("");
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.parseColor("#88E788")); // Back to starting color
                    System.out.println("Removed flag at (" + i + ", " + j + ")");
                } else if (currentText.equals("")) {
                    // Add flag
                    tv.setText(getString(R.string.flag));
                    tv.setTextColor(Color.RED);
                    tv.setBackgroundColor(Color.YELLOW);
                    System.out.println("Added flag at (" + i + ", " + j + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR in onClickTV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // NEW METHOD: Reveal mines one by one with animation
    private void revealAllMinesSequentially() {
        try {
            System.out.println("Starting sequential mine reveal...");

            // Collect all mine positions
            ArrayList<int[]> minePositions = new ArrayList<>();
            String mineText = getString(R.string.mine);

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (isMine(i, j)) {
                        int index = i * COLUMN_COUNT + j;
                        if (index >= 0 && index < cell_tvs.size()) {
                            TextView cell = cell_tvs.get(index);
                            String currentText = cell.getText().toString();

                            // Only add mines that aren't already revealed
                            if (!currentText.equals(mineText)) {
                                minePositions.add(new int[]{i, j});
                            }
                        }
                    }
                }
            }

            System.out.println("Found " + minePositions.size() + " mines to reveal");

            // Reveal mines one by one with delays
            revealMineAtIndex(minePositions, 0);

        } catch (Exception e) {
            System.out.println("ERROR in revealAllMinesSequentially: " + e.getMessage());
            e.printStackTrace();
            // Fallback - go directly to results
            showResults();
        }
    }

    // Recursive method to reveal mines with delays
    private void revealMineAtIndex(ArrayList<int[]> minePositions, int currentIndex) {
        try {
            if (currentIndex >= minePositions.size()) {
                // All mines revealed, now transition to results
                System.out.println("All mines revealed, transitioning to results...");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Final delay completed, calling showResults()");
                        showResults();
                    }
                }, 1500); // Increased to 1.5 second delay after last mine
                return;
            }

            // Reveal current mine
            int[] position = minePositions.get(currentIndex);
            int row = position[0];
            int col = position[1];
            int index = row * COLUMN_COUNT + col;

            if (index >= 0 && index < cell_tvs.size()) {
                TextView cell = cell_tvs.get(index);
                cell.setText(getString(R.string.mine));
                cell.setTextColor(Color.RED);
                cell.setBackgroundColor(Color.parseColor("#ffcccb")); // Light coral
                System.out.println("Revealed mine " + (currentIndex + 1) + " at (" + row + ", " + col + ")");
            }

            // Schedule next mine reveal
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    revealMineAtIndex(minePositions, currentIndex + 1);
                }
            }, 400); // Slightly increased delay between mines

        } catch (Exception e) {
            System.out.println("ERROR in revealMineAtIndex: " + e.getMessage());
            e.printStackTrace();
            // Fallback - go directly to results
            System.out.println("Error occurred, falling back to showResults()");
            showResults();
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        // Check all 8 adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // Skip center cell
                if (isMine(row + i, col + j)) count++;
            }
        }

        return count;
    }

    private boolean checkWinCondition() {
        // Check if all non-mine cells are revealed
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                // If found a cell that is not a mine and not revealed, game is not won
                if (!isMine(i, j) && !revealed[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private void endGame(boolean won) {
        try {
            gameOver = true;
            stopTimer();
            System.out.println("Game ended. Result: " + (won ? "WON" : "LOST"));

        } catch (Exception e) {
            System.out.println("ERROR in endGame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showResults() {
        try {
            System.out.println("=== SHOW RESULTS CALLED ===");

            // Get elapsed time
            long elapsedTime = 0;
            if (startTime > 0) {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            }

            boolean won = checkWinCondition();
            System.out.println("Game state - Won: " + won + ", Time: " + elapsedTime + " seconds");
            System.out.println("Creating intent for ResultActivity...");

            // Show results in ResultActivity screen
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("GAME_WON", won);
            intent.putExtra("TIME_ELAPSED", (int) elapsedTime);

            System.out.println("Starting ResultActivity...");
            startActivity(intent);
            System.out.println("ResultActivity started successfully");

            // DON'T reset game here - let ResultActivity handle the return

        } catch (Exception e) {
            System.out.println("ERROR in showResults: " + e.getMessage());
            e.printStackTrace();

            // If there's an error, try a simple approach
            System.out.println("Attempting fallback ResultActivity launch...");
            try {
                Intent fallbackIntent = new Intent(MainActivity.this, ResultActivity.class);
                fallbackIntent.putExtra("GAME_WON", false);
                fallbackIntent.putExtra("TIME_ELAPSED", 0);
                startActivity(fallbackIntent);
            } catch (Exception e2) {
                System.out.println("Fallback also failed: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public void newGame() {
        resetTimer();
        initializeMineField();
        resetGrid();
        gameStarted = false;
        gameOver = false;
        System.out.println("New game started!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer when activity is destroyed
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only reset game if we're returning from ResultActivity
        // Check if game is over and we need a fresh start
        if (gameOver) {
            System.out.println("Returning from results, starting new game");
            newGame();
        }
    }
}