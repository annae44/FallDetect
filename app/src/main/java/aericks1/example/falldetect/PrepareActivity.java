package aericks1.example.falldetect;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class PrepareActivity extends AppCompatActivity implements SensorEventListener {
    TextView timerTextView;
    long startTime = -1;

    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private TextView mSensorX;
    private TextView mSensorY;
    private TextView mSensorZ;

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
            } else if (seconds >= 10){
                 onStart();
            } else {
                Intent CompleteIntent = new Intent(getApplicationContext(), CompleteActivity.class);
                startActivity(CompleteIntent);
            }

            timerTextView.setText(String.format("%02d seconds", seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prepare);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        timerHandler.postDelayed(timerRunnable, 0);
        timerTextView = (TextView) findViewById(R.id.timer_text_view);

        mSensorX = (TextView) findViewById(R.id.sensor_text_view_x);
        mSensorY = (TextView) findViewById(R.id.sensor_text_view_y);
        mSensorZ = (TextView) findViewById(R.id.sensor_text_view_z);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float currentValue = sensorEvent.values[0];
        mSensorX.setText(getResources().getString(
                R.string.sensor_text_view_x, currentValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

