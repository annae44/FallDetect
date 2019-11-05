package aericks1.example.falldetect;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;


public class PrepareActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MESSAGE_ID = 0;
    TextView timerTextView;
    long startTime = -1;

    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;

    private TextView mTextSensor = null;

    boolean during = false;


    ArrayList<Float> sensorArray = new ArrayList<Float>();

    private static Context c;
    public static Context getContext() {
        return c;
    }

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

            if (startTime == -1) {
                startTime = (int) (System.currentTimeMillis() / 1000);
            }

            long millis = System.currentTimeMillis();
            int seconds = (int) (millis / 1000) - (int) startTime;
            seconds = seconds % 60;

            if (!during) {
                if (seconds >= 2) {
                    startTime = (int) (System.currentTimeMillis() / 1000);
                    during = true;
                    ToneGenerator tone1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tone1.startTone(ToneGenerator.TONE_SUP_PIP, 800);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(800);
                }

            } else if (seconds >= 5) {
                onStop();
                finish();
                Log.i(MainActivity.TAG, "in the else of run()");
                doneFlag = true;
                ToneGenerator tone1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                tone1.startTone(ToneGenerator.TONE_SUP_PIP, 1000);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(800);
                try {
                    Writer.main(sensorArray, c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendEmail();
                try {
                    Writer.main(sensorArray, c);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PrepareActivity.this, "Cannot Write to File.", Toast.LENGTH_SHORT).show();
                }
            }

            if (!doneFlag) {
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
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float currentValueX = sensorEvent.values[0];
            float currentValueY = sensorEvent.values[1];
            float currentValueZ = sensorEvent.values[2];
            Log.i(MainActivity.TAG, "accel " + currentValueX + " " + currentValueY + " " + currentValueZ);
            String s = String.format("Accel %.2f %.2f %.2f", currentValueX, currentValueY, currentValueZ);
            mTextSensor.setText(s);

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
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
        Log.i("Send email", "");
        String[] TO = {"aericks1@uvm.edu"};
        //String[] TO = {"afronhof@uvm.edu"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        Uri file = null;
        String filename = "sensorData.csv";
        File fileLocation = new File(c.getExternalCacheDir(), filename);
        //file = Uri.parse("file://"+fileLocation);
        file = FileProvider.getUriForFile(PrepareActivity.this,
                getString(R.string.file_provider_authority), fileLocation);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FallDetect Results");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here are the results:\n");
        c.grantUriPermission("aericks1.example.falldetect", file, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.putExtra(Intent.EXTRA_STREAM, file);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(PrepareActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}