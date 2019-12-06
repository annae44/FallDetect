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
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class PrepareActivity extends AppCompatActivity implements SensorEventListener {
    // initialize random variables
    private static final int MESSAGE_ID = 0;
    TextView timerTextView;
    private TextToSpeech t4;
    long startTime = -1;
    int countdown = 0;
    boolean during = false;

    // initialize sensor variables
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private TextView mTextSensor = null;
    ArrayList<Double> sensorArray = new ArrayList<Double>();

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
                if (seconds >= 5) {
                    // start the test, make a sound, and make a vibration
                    startTime = (int) (System.currentTimeMillis() / 1000);
                    during = true;
                    ToneGenerator tone1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    tone1.startTone(ToneGenerator.TONE_SUP_PIP, 800);
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(800);
                }

            // if its during the test and its been the duration of the test
            } else if (seconds >= 30) {
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
                String s = "";
                if (!during) {
                    countdown = 5 - seconds;
                    s = String.format("This test will begin in:\n%d seconds", countdown);
                } else {
                    countdown = 30 - seconds;
                    s = String.format("Seconds remaining:\n%d seconds", countdown);
                }
                //  display the seconds on the screen
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

        c = getApplicationContext();

        // initiate text to speech to allow directions to be read out loud
        t4 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    t4.setLanguage(Locale.US);
                    Log.i("TTS", "Speaker Initialized");

                    String data = getString(R.string.prepare_text1);
                    int speechStatus = t4.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            Writer.main(sensorArray, c, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // send email containing files
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

    public void onPause(){
        if(t4 !=null){
            t4.stop();
            t4.shutdown();
        }
        super.onPause();
    }

    protected void sendEmail() {
        // set recipients
        Log.i("Send email", "");
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        // retrieve content - static file
        Uri file1 = null;
        String filename1 = "staticSensorData.csv";
        File fileLocation1 = new File(c.getExternalCacheDir(), filename1);
        file1 = FileProvider.getUriForFile(PrepareActivity.this,
                getString(R.string.file_provider_authority), fileLocation1);

        // retrieve content - dynamic file
        Uri file2 = null;
        String filename2 = "dynamicSensorData.csv";
        File fileLocation2 = new File(c.getExternalCacheDir(), filename2);
        file2 = FileProvider.getUriForFile(PrepareActivity.this,
                getString(R.string.file_provider_authority), fileLocation2);

        // add files to URI array list
        ArrayList<Uri> files = new ArrayList<Uri>();
        files.add(file1);
        files.add(file2);

        // create email
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FallDetect Results");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here are the results:\n");

        // handle permissions
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