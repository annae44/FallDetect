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
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;


public class PrepareActivity extends AppCompatActivity implements SensorEventListener {
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

    // initiate and retrieve C++ integration files
    private static final String TAG = "SimpleJNI";
    static {
        System.loadLibrary("native-lib");
    }
    public native String stringFromJNI();
    public native double[] computeJNI(int num, double inArrayStatic[], double inArrayDynamic[]);

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

            // Reset Timer
            if (startTime == -1) {
                startTime = (int) (System.currentTimeMillis() / 1000);
            }

            // Calculate current seconds from start
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
        setContentView(R.layout.prepare);

        // retrieve accelerometer data
        mTextSensor = findViewById(R.id.sensor_text_view_x);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        String sensor_error = getResources().getString(R.string.error_no_sensor);

        c = getApplicationContext();
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

        Log.i(TAG, stringFromJNI());

        // write to file
        try {
            Writer.main(sensorArray, c, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TODO: JNI do processing -- pass in array of x, y, and z and returns transposed array



        //double[] doubleStaticArray = new double[sensorArrayStatic.size()];

        double[] doubleStaticArray = new double[sensorArray.size()];

        // change once static array gets passed in
        for (int i = 0; i < sensorArray.size(); i++){
            doubleStaticArray[i] = sensorArray.get(i);
        }

        double[] doubleDynamicArray = new double[sensorArray.size()];
        for (int i = 0; i < sensorArray.size(); i++){
            doubleDynamicArray[i] = sensorArray.get(i);
        }
        Log.i(TAG, "Before JNI");
        double[] transposedArray = computeJNI(1, doubleStaticArray, doubleDynamicArray);

        Log.i(TAG, "After JNI");
        for (int i=0; i<4; ++i) {
            Log.i(TAG, "transposed[" + i + "] = " + transposedArray[i]);
        }




        // send email containing file
        sendEmail();
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

    protected void sendEmail() {
        // set recipients
        Log.i("Send email", "");
        String[] TO = {"aericks1@uvm.edu"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        // retrieve content - static file
        Uri file1 = null;
        String filename1 = "sensorData1.csv";
        File fileLocation1 = new File(c.getExternalCacheDir(), filename1);
        file1 = FileProvider.getUriForFile(PrepareActivity.this,
                getString(R.string.file_provider_authority), fileLocation1);

        // retrieve content - dynamic file
        Uri file2 = null;
        String filename2 = "sensorData2.csv";
        File fileLocation2 = new File(c.getExternalCacheDir(), filename2);
        file2 = FileProvider.getUriForFile(PrepareActivity.this,
                getString(R.string.file_provider_authority), fileLocation2);

        ArrayList<Uri> files = new ArrayList<Uri>();
        files.add(file1);
        files.add(file2);

        // create email
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FallDetect Results");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here are the results:\n");


        c.grantUriPermission("aericks1.example.falldetect", file1, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

        // send the email
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(PrepareActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}