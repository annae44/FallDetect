package aericks1.example.falldetect;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

public class Writer extends Application {

    public static void main(ArrayList<Double> array, Context context, int fileNum) throws IOException {
        // fileNum = 1: the static file
        // fileNum = 2: the dynamic file
        // create file path
        String filename;
        if (fileNum == 1) {
            filename = context.getExternalCacheDir() + "/staticSensorData.csv";
        } else {
            filename = context.getExternalCacheDir() + "/dynamicSensorData.csv";
        }

        // open ostream
        FileOutputStream file = new FileOutputStream(filename);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);

        // add all the sensor data to the files
        for (int j = 0; j < array.size(); j += 3) {
            outputStreamWriter.write(array.get(j).toString() + ", ");
            outputStreamWriter.write(array.get(j + 1).toString()  + ", ");
            outputStreamWriter.write(array.get(j + 2).toString());
            outputStreamWriter.write("\n");
        }

        // close file
        outputStreamWriter.close();
    }

}
