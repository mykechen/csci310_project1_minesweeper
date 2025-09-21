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

                    // format time in seconds
                    String timeText = String.valueOf(seconds);
                    timerDisplay.setText(timeText);

                    // update every second
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void startTimer() {
        if (!isTimerRunning) {
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
            return;
        }

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if we're in pickaxe mode or flag mode
                isPickaxeMode = !isPickaxeMode;

                if (isPickaxeMode) {
                    modeButton.setText(getString(R.string.pick)); // switch to pickaxe
                } else {
                    modeButton.setText(getString(R.string.flag)); // switch to flag
                }
            }
        });
    }

    private void initializeMineField() {
        // initialize minefield and revealed arrays
        minefield = new boolean[COLUMN_COUNT][COLUMN_COUNT];
        revealed = new boolean[COLUMN_COUNT][COLUMN_COUNT];

        // clear both arrays for new game
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                minefield[i][j] = false;
                revealed[i][j] = false;
            }
        }

        // place mines randomly
        java.util.Random random = new java.util.Random();
        int minesPlaced = 0;

        while (minesPlaced < MINE_COUNT) {
            int row = random.nextInt(10);
            int col = random.nextInt(10);

            // check if this position doesn't already have a mine
            if (!minefield[row][col]) {
                minefield[row][col] = true;
                minesPlaced++;
                System.out.println("Mine placed at: (" + row + ", " + col + ")");
            }
        }

        System.out.println("Total mines placed: " + minesPlaced);
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
        for (TextView cell : cell_tvs) {
            cell.setText("");
            cell.setTextColor(Color.BLACK);
            cell.setBackgroundColor(Color.parseColor("#88E788"));
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
        // check if game over - if so, don't process any more moves
        if (gameOver) {
            return;
        }

        // start timer on first click
        if (!isTimerRunning) {
            startTimer();
            gameStarted = true;
        }

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);

        // validate cell index
        if (n < 0 || n >= cell_tvs.size()) {
            return;
        }

        int i = n / COLUMN_COUNT;
        int j = n % COLUMN_COUNT;

        // check if coords are valid
        if (i < 0 || i >= 10 || j < 0 || j >= 10) {
            return;
        }

        System.out.println("Clicked cell at (" + i + ", " + j + "), mode: " + (isPickaxeMode ? "dig" : "flag"));

        if (isPickaxeMode) {
            // Digging mode

            // can't dig flagged cells
            String currentText = tv.getText().toString();
            String flagText = getString(R.string.flag);
            if (currentText.equals(flagText)) {
                System.out.println("Cannot dig flagged cell at (" + i + ", " + j + ")");
                return;
            }

            // can't dig already revealed cells
            if (revealed[i][j]) {
                System.out.println("Cell (" + i + ", " + j + ") already revealed");
                return;
            }

            if (isMine(i, j)) {
                // hit a mine - game over (LOSE)

                // mark as revealed and show mine
                revealed[i][j] = true;
                tv.setText(getString(R.string.mine));
                tv.setTextColor(Color.RED);
                tv.setBackgroundColor(Color.parseColor("#ffcccb")); // Light coral

                // end game and reveal all mines
                endGame(false); // false = lost

                revealAllMines();

            } else {
                // Safe cell
                revealCellRecursively(i, j); // reveal all adjacent cells

                // check for win condition after revealing
                if (checkWinCondition()) {
                    endGame(true); // true = won

                    // go to results screen
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showResults();
                        }
                    }, 1000);
                }
            }
        } else {
            // Flagging mode

            // can't flag an already revealed cell
            if (revealed[i][j]) {
                return;
            }

            String currentText = tv.getText().toString();
            String flagText = getString(R.string.flag);

            // Toggle flag
            if (currentText.equals(flagText)) {
                // Remove flag
                tv.setText("");
                tv.setTextColor(Color.BLACK);
            } else if (currentText.equals("")) {
                // Add flag
                tv.setText(getString(R.string.flag));
                tv.setTextColor(Color.RED);
            }
        }
    }

    private void revealCellRecursively(int row, int col) {
        // check boundaries
        if (row < 0 || row >= 10 || col < 0 || col >= 10) {
            return;
        }

        // don't reveal if already revealed, is a mine
        if (revealed[row][col] || isMine(row, col)) {
            return;
        }

        int index = row * COLUMN_COUNT + col;
        if (index < 0 || index >= cell_tvs.size()) {
            return;
        }

        TextView cell = cell_tvs.get(index);
        String currentText = cell.getText().toString();
        String flagText = getString(R.string.flag);

        // don't reveal flagged cells
        if (currentText.equals(flagText)) {
            return;
        }

        // mark as revealed
        revealed[row][col] = true;

        // count adjacent mines
        int adjacentMines = countAdjacentMines(row, col);

        // set text and appearance
        cell.setText(adjacentMines == 0 ? "" : String.valueOf(adjacentMines));
        cell.setTextColor(Color.BLACK);
        cell.setBackgroundColor(Color.parseColor("#E0E0E0")); // Revealed color

        //fIf this cell has no adjacent mines, recursively reveal all adjacent cells
        if (adjacentMines == 0) {
            // reveal all 8 adjacent cells
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue; // skip center cell
                    revealCellRecursively(row + i, col + j);
                }
            }
        }
    }

    private void revealAllMines() {
        // get all mine positions
        ArrayList<int[]> minePositions = new ArrayList<>();
        String mineText = getString(R.string.mine);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (isMine(i, j)) {
                    int index = i * COLUMN_COUNT + j;
                    if (index >= 0 && index < cell_tvs.size()) {
                        TextView cell = cell_tvs.get(index);
                        String currentText = cell.getText().toString();

                        // only add mines that aren't already revealed
                        if (!currentText.equals(mineText)) {
                            minePositions.add(new int[]{i, j});
                        }
                    }
                }
            }
        }

        System.out.println("Found " + minePositions.size() + " mines to reveal");

        // reveal mines one by one with delays
        revealMineAtIndex(minePositions, 0);
    }

    private void revealMineAtIndex(ArrayList<int[]> minePositions, int currentIndex) {
        if (currentIndex >= minePositions.size()) {
            // check if all mines revealed to transition to results

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Final delay completed, calling showResults()");
                    showResults();
                }
            }, 1500);
            return;
        }

        // reveal current mine
        int[] position = minePositions.get(currentIndex);
        int row = position[0];
        int col = position[1];
        int index = row * COLUMN_COUNT + col;

        if (index >= 0 && index < cell_tvs.size()) {
            TextView cell = cell_tvs.get(index);
            cell.setText(getString(R.string.mine));
            cell.setTextColor(Color.RED);
            cell.setBackgroundColor(Color.parseColor("#ffcccb")); // Light coral
        }

        // get next mine reveal
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealMineAtIndex(minePositions, currentIndex + 1);
            }
        }, 500);
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        // check all 8 adjacent cells
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue; // skips the center call
                if (isMine(row + i, col + j)) count++;
            }
        }

        return count;
    }

    private boolean checkWinCondition() {
        // check if all non-mine cells are revealed
        for (int i = 0; i < COLUMN_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                // if found a cell that is not a mine and not revealed, game is not over
                if (!isMine(i, j) && !revealed[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private void endGame(boolean won) {
        gameOver = true;
        stopTimer();
    }

    private void showResults() {
        // get elapsed time
        long elapsedTime = 0;
        if (startTime > 0) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        }

        boolean won = checkWinCondition();

        // show results in ResultActivity screen
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("GAME_WON", won);
        intent.putExtra("TIME_ELAPSED", (int) elapsedTime);

        // goes to results screen
        startActivity(intent);
    }

    public void newGame() {
        resetTimer();
        initializeMineField();
        resetGrid();
        gameStarted = false;
        gameOver = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameOver) {
            newGame();
        }
    }
}