package aericks1.example.falldetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBeginButton;
    public final static String TAG = "FallDetect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBeginButton = (Button) findViewById(R.id.begin_button);

        mBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button Clicked");

                // open new activity using an intent
                Intent IntroIntent = new Intent(getApplicationContext(), IntroductionActivity.class);
                startActivity(IntroIntent);
            }
        });

    }
}
