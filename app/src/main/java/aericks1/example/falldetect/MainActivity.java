package aericks1.example.falldetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBeginButton;

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
