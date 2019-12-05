package aericks1.example.falldetect;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;

public class StaticActitvity extends AppCompatActivity implements SensorEventListener {
    // initialize random variables
    private static final int MESSAGE_ID = 0;
    TextView timerTextView;
    long startTime = -1;
    boolean during = false;

    // initialize sensor variables
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private TextView mTextSensor = null;
    ArrayList<Double> sensorArray = new ArrayList<Double>();


    private static final String ARRAY_OF_VALUES = "aericks1.example.falldetect.array_of_values";


    // context method
    private static Context c;
    public static Context getContext() {
        return c;
    }

    // Create timer handler
    @SuppressLint("HandlerLeak")
    Handler timerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String target = (String) msg.obj;
            Log.i(MainActivity.TAG, "got a response: " + target);
            timerTextView.setText(target);
        }
    };

    final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            boolean doneFlag = false;

            // reset Timer
            if (startTime == -1) {
                startTime = (int) (System.currentTimeMillis() / 1000);
            }

            // calculate current seconds from start
            long millis = System.currentTimeMillis();
            int seconds = (int) (millis / 1000) - (int) startTime;
            seconds = seconds % 60;

            // if its before the test begins
            if (!during) {
                // if its been more than x seconds from start
                if (seconds >= 2) {
                    // start the test, make a sound, and make a vibration
                    startTime = (int) (System.currentTimeMillis() / 1000);
                    during = true;
                    ToneGenerator tone1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tone1.startTone(ToneGenerator.TONE_SUP_PIP, 800);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(800);
                }

                // if its during the test and its been the duration of the test
            } else if (seconds >= 5) {
                // stop the test, make a sounds, and make a vibration
                onStop();
                finish();
                Log.i(MainActivity.TAG, "in the else of run()");
                doneFlag = true;
                ToneGenerator tone1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                tone1.startTone(ToneGenerator.TONE_SUP_PIP, 1000);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);
            }

            // if the test is not complete
            if (!doneFlag) {
                //  display the seconds on the screen
                String s = String.format("%d seconds", seconds);
                timerHandler.obtainMessage(MESSAGE_ID, s).sendToTarget();
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Log.i(MainActivity.TAG, "onCreate()");
        setContentView(R.layout.static_retrieval);

        //attempt of passing in array. I believe I have the order mixed up
        //double [] arr = getIntent().getDoubleArrayExtra(ARRAY_OF_VALUES);

        // retrieve accelerometer data
        mTextSensor = findViewById(R.id.sensor_text_view_x);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        c = getApplicationContext();
    }


    public static Intent newIntent(Context packageContext, double [] arr) {
        Intent intent = new Intent(packageContext, CompleteActivity.class);
        intent.putExtra(ARRAY_OF_VALUES, arr);
        return intent;
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



        // write to file
        try {
            Writer.main(sensorArray, c, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // when there is a change in the sensor, add that value to the sensor array

        int sensorType = sensorEvent.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float currentValueX = sensorEvent.values[0];
            float currentValueY = sensorEvent.values[1];
            float currentValueZ = sensorEvent.values[2];
            Log.i(MainActivity.TAG, "accel " + currentValueX + " " + currentValueY + " " + currentValueZ);
            String s = String.format("Accel %.2f %.2f %.2f", currentValueX, currentValueY, currentValueZ);
            mTextSensor.setText(s);

            double x = sensorEvent.values[0];
            double y = sensorEvent.values[1];
            double z = sensorEvent.values[2];


            // add x, y, and z to the sensor array
            sensorArray.add(x);
            sensorArray.add(y);
            sensorArray.add(z);

        } else {
            Log.i(MainActivity.TAG, "sensor type " + sensorType);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
