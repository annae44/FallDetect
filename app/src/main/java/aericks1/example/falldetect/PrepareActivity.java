package aericks1.example.falldetect;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class PrepareActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MESSAGE_ID = 0;
    TextView timerTextView;
    long startTime = -1;

    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorLight;

    private TextView mTextSensor = null;
    //private TextView mTextSensorY;
    //private TextView mTextSensorZ;

    boolean during = false;
    boolean done = false;

    Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String target = (String) msg.obj;
            Log.i(MainActivity.TAG, "got a response: " + target);
            timerTextView.setText(target);
        }
    };

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            boolean doneFlag = false;

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
                Log.i(MainActivity.TAG, "in the else of run()");
                doneFlag = true;
            }

            if ( ! doneFlag ) {
                //timerTextView.setText(String.format("%02d seconds", seconds));
                String s = String.format("%d seconds", seconds);
                timerHandler.obtainMessage(MESSAGE_ID, s).sendToTarget();
                timerHandler.postDelayed(this, 500);
            } else {
                return;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.TAG, "onCreate()");
        setContentView(R.layout.prepare);

        //jdh
        mTextSensor = findViewById(R.id.sensor_text_view_x);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        TextView mTextSensor;

        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        String sensor_error = getResources().getString(R.string.error_no_sensor);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);

            timerHandler.postDelayed(timerRunnable, 0);
            timerTextView = (TextView) findViewById(R.id.timer_text_view);
        } else {
            Log.i(MainActivity.TAG, "can't register accelerometer");
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
        if (sensorType == Sensor.TYPE_LIGHT) {
            float currentValueX = sensorEvent.values[0];
            mTextSensor.setText(String.format("Light Sensor X: %.2f", currentValueX));
        } else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float currentValueX = sensorEvent.values[0];
            float currentValueY = sensorEvent.values[1];
            float currentValueZ = sensorEvent.values[2];
            Log.i(MainActivity.TAG, "accel " + currentValueX + " " + currentValueY + " " + currentValueZ);
            String s = String.format("Accel %.2f %.2f %.2f", currentValueX, currentValueY, currentValueZ);
            mTextSensor.setText(s);
        } else {
            Log.i(MainActivity.TAG, "sensor type " + sensorType);
        }
        //float currentValueY = sensorEvent.values[1];
        //mTextSensorY.setText(getResources().getString(R.string.sensor_text_view_y, currentValueY));

        //float currentValueZ = sensorEvent.values[2];
        //mTextSensorZ.setText(getResources().getString(R.string.sensor_text_view_z, currentValueZ));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

