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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // get the result data from the intent
        Intent intent = getIntent();
        boolean gameWon = intent.getBooleanExtra("GAME_WON", false);
        int timeElapsed = intent.getIntExtra("TIME_ELAPSED", 0);

        TextView timeText = findViewById(R.id.time_text);
        TextView resultText = findViewById(R.id.result_text);
        TextView congratsText = findViewById(R.id.congrats_text);
        Button playAgainButton = findViewById(R.id.play_again_button);


        // display the elapsed time
        String timeMessage = "Used " + timeElapsed + " second" + (timeElapsed == 1 ? "" : "s");
        timeText.setText(timeMessage);

        // display the result
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

        // play again button
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // return to the main activity for a new game
                Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);

                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(mainIntent);

                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("ResultActivity destroyed");
    }

    @Override
    public void onBackPressed() {
        // override back button to go to main activity instead of previous game state
        super.onBackPressed();
        Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}