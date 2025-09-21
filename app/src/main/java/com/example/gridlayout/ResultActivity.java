package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Hide the action bar to match main activity
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Get the result data from the intent
        Intent intent = getIntent();
        boolean gameWon = intent.getBooleanExtra("GAME_WON", false);
        int timeElapsed = intent.getIntExtra("TIME_ELAPSED", 0);

        // Debug output
        System.out.println("ResultActivity - Game Won: " + gameWon + ", Time: " + timeElapsed);

        // Find UI elements
        TextView timeText = findViewById(R.id.time_text);
        TextView resultText = findViewById(R.id.result_text);
        TextView congratsText = findViewById(R.id.congrats_text);
        Button playAgainButton = findViewById(R.id.play_again_button);

        // Check if UI elements were found
        if (timeText == null || resultText == null || congratsText == null || playAgainButton == null) {
            System.out.println("ERROR: One or more UI elements not found in ResultActivity");
            return;
        }

        // Display the elapsed time
        String timeMessage = "Used " + timeElapsed + " second" + (timeElapsed == 1 ? "" : "s");
        timeText.setText(timeMessage);

        // Display the result based on win/lose
        if (gameWon) {
            resultText.setText("You won");
            resultText.setTextColor(Color.parseColor("#4CAF50"));
            congratsText.setText("Good job!");
            congratsText.setTextColor(Color.parseColor("#666666"));
        } else {
            resultText.setText("You lost");
            resultText.setTextColor(Color.parseColor("#F44336"));
            congratsText.setText("Try again.");
            congratsText.setTextColor(Color.parseColor("#666666"));
        }

        // Set up the play again button click listener
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Play Again button clicked");

                // Return to the main activity for a new game
                Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);

                // Clear the activity stack and start fresh
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(mainIntent);

                // Close this results activity
                finish();
            }
        });

        System.out.println("ResultActivity setup complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("ResultActivity destroyed");
    }

    @Override
    public void onBackPressed() {
        // Override back button to go to main activity instead of previous game state
        super.onBackPressed();
        Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}