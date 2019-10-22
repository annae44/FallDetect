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

    private TextView mTextSensorX;
    //private TextView mTextSensorY;
    //private TextView mTextSensorZ;

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

        TextView mTextSensorX;
        //mTextSensorY = (TextView) findViewById(R.id.sensor_text_view_y);
        //mTextSensorZ = (TextView) findViewById(R.id.sensor_text_view_z);

        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        //if (mSensorAccelerometer == null) {
         //   mTextSensorX.setText(getResources().getString(R.string.error_no_sensor));
            //mTextSensorY.setText(sensor_error);
            //mTextSensorZ.setText(sensor_error);
        //}


        timerHandler.postDelayed(timerRunnable, 0);
        timerTextView = (TextView) findViewById(R.id.timer_text_view);
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
        int sensorType = sensorEvent.sensor.getType();
        float currentValueX = sensorEvent.values[0];
        //mTextSensorX.setText(getResources().getString(R.string.sensor_text_view_x, currentValueX));
        mTextSensorX.setText(String.format("Light Sensor X: %1$.2f", currentValueX));

        //float currentValueY = sensorEvent.values[1];
        //mTextSensorY.setText(getResources().getString(R.string.sensor_text_view_y, currentValueY));

        //float currentValueZ = sensorEvent.values[2];
        //mTextSensorZ.setText(getResources().getString(R.string.sensor_text_view_z, currentValueZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

