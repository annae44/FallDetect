package aericks1.example.falldetect;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CompleteActivity extends AppCompatActivity {
    private Button mStartAgain;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete);

        mStartAgain = (Button) findViewById(R.id.start_again_button);

        mStartAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button Clicked");

                // open new activity using an intent
                Intent IntroIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(IntroIntent);

            }
        });
    }

}
