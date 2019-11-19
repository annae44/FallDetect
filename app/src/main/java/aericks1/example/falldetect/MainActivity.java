package aericks1.example.falldetect;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // initialize  variables
    private Button mBeginButton;
    public final static String TAG = "FallDetect";
    public final static int INTRO_REQUEST = 1;
    public final static int PREPARE_REQUEST = 2;
    public final static int COMPLETE_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_main);

        mBeginButton = (Button) findViewById(R.id.begin_button);

        mBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button Clicked");

                // open new activity using an intent
                Intent IntroIntent = new Intent(getApplicationContext(), IntroductionActivity.class);
                startActivityForResult(IntroIntent, INTRO_REQUEST);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle the different activity requests
        if (requestCode == INTRO_REQUEST) {
            Intent intent = new Intent(getApplicationContext(), PrepareActivity.class);
            startActivityForResult(intent, PREPARE_REQUEST);
        } else if (requestCode == PREPARE_REQUEST){
            Intent CompleteIntent = new Intent(getApplicationContext(), CompleteActivity.class);
            startActivity(CompleteIntent);
        } else if (requestCode == COMPLETE_REQUEST) {
            Intent IntroIntent = new Intent(getApplicationContext(), IntroductionActivity.class);
            startActivityForResult(IntroIntent, INTRO_REQUEST);
        }

    }
}
