package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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

        // Dynamically create the cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                TextView tv = new TextView(this);
                tv.setHeight(dpToPixel(32));
                tv.setWidth(dpToPixel(32));
                tv.setTextSize(16);
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
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

                    // Debug: Print to console
                    System.out.println("Timer update: " + seconds + " seconds");

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

    private void toggleMode(){
        modeButton = findViewById(R.id.mode_button);

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle mode and update icon in one function
                isPickaxeMode = !isPickaxeMode;

                if (isPickaxeMode) {
                    modeButton.setText(getString(R.string.pick)); // switch to pickaxe️
                } else {
                    modeButton.setText(getString(R.string.flag)); // switch to flag
                }
            }
        });
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n = 0; n < cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view) {
        // Start timer on first click
        if (!isTimerRunning) {
            System.out.println("First click detected - starting timer");
            startTimer();
        }

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n / COLUMN_COUNT;
        int j = n % COLUMN_COUNT;
        tv.setText(String.valueOf(i) + String.valueOf(j));

        if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
        } else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up timer when activity is destroyed
        stopTimer();
    }

    public void newGame() {
        resetTimer();
        // Add any other game reset logic here
    }
}