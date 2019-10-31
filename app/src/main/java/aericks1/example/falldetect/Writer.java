package aericks1.example.falldetect;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

public class Writer extends Application {

    private static Context sContext;
    @Override
    public void onCreate() {
        sContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void main(ArrayList<Float> array) throws IOException {
        /**
        //String csv = "\\Desktop\\Class\\CS275\\GroupProject\\sensorData.csv"; //CHANGE AS NEEDED

        FileWriter writer = new FileWriter(csv);
        //CSVWriter writer = new CSVWriter(new FileWriter(csv));

        for (int j = 0; j < array.size(); j+=3) {
            writer.append(array.get(j).toString());
            writer.append(array.get(j+1).toString());
            writer.append(array.get(j+2).toString());
            writer.write("\n");
        }

        writer.close();

         **/

        //String filename = "/Users/annafron21/Desktop/sensorData.rtf";
        Context context = getContext();
        String filename = context.getFilesDir().getPath() + "/sensorData.csv";
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);

        for (int j = 0; j < array.size(); j+=3) {
            writer.append(array.get(j).toString());
            writer.append(array.get(j+1).toString());
            writer.append(array.get(j+2).toString());
            writer.write("\n");
        }

        writer.close();
    }

}
