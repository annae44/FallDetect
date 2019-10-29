package aericks1.example.falldetect;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Writer {

    public static void main(ArrayList<Float> array) throws IOException {
        String csv = "\\Desktop\\Class\\CS275\\GroupProject\\sensorData.csv"; //CHANGE AS NEEDED

        FileWriter writer = new FileWriter(csv);
        //CSVWriter writer = new CSVWriter(new FileWriter(csv));

        for (int j = 0; j < array.size(); j+=3) {
            writer.append(array.get(j).toString());
            writer.append(array.get(j+1).toString());
            writer.append(array.get(j+2).toString());
            writer.write("\n");
        }

        writer.close();

    }
}
