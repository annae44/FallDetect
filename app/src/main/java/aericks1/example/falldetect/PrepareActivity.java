package aericks1.example.falldetect;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class PrepareActivity extends AppCompatActivity {
    TextView timerTextView;
    long startTime = -1;

    boolean during = false;
    boolean done = false;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (startTime == -1) {
                startTime = (int) (System.currentTimeMillis() / 1000);
            }

            long millis = System.currentTimeMillis();
            int seconds = (int) (millis / 1000) - (int)startTime;
            seconds = seconds % 60;

            if(!during) {
                if (seconds >= 5){
                    startTime = (int) (System.currentTimeMillis() / 1000);
                    during = true;
                }
            } else if (seconds == 10){
                Intent CompleteIntent = new Intent(getApplicationContext(), CompleteActivity.class);
                startActivity(CompleteIntent);
            }

            timerTextView.setText(String.format("%02d", seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare);

        System.out.println(startTime);

        timerHandler.postDelayed(timerRunnable, 0);
        timerTextView = (TextView) findViewById(R.id.timer_text_view);

    }
}

