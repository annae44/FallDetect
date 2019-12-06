package aericks1.example.falldetect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class IntroductionActivity extends AppCompatActivity {
    private Button mStartButton;
    private TextToSpeech t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.introduction);

        mStartButton = (Button) findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // initiate text to speech to allow directions to be read out loud
        t3 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    t3.setLanguage(Locale.US);
                    Log.i("TTS", "Speaker Initialized");

                    String data = getString(R.string.intro_label) + ". " + getString(R.string.intro_description);
                    int speechStatus = t3.speak(data, TextToSpeech.QUEUE_FLUSH, null);

                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public void onPause(){
        if(t3 !=null){
            t3.stop();
            t3.shutdown();
        }
        super.onPause();
    }
}
