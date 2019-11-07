package aericks1.example.falldetect;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class Writer extends Application {

    public static void main(ArrayList<Float> array, Context context) throws IOException {

        String filename = context.getExternalCacheDir() + "/sensorData.csv";

        FileOutputStream file = new FileOutputStream(filename);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);

        for (int j = 0; j < array.size(); j += 3) {
            outputStreamWriter.write(array.get(j).toString() + ", ");
            outputStreamWriter.write(array.get(j + 1).toString()  + ", ");
            outputStreamWriter.write(array.get(j + 2).toString());
            outputStreamWriter.write("\n");
        }

        outputStreamWriter.close();
    }

}
