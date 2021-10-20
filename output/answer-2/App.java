import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.*;

final class Data {
    private Data() {

    }

    private static final String BASE_PATH = System.getProperty("user.dir");
    private static final String INPUT = BASE_PATH.substring(0, BASE_PATH.lastIndexOf("output")) + "input";
    private static final String OUTPUT = BASE_PATH;
    private static final String _ques1 = "\\question-1\\main.csv";
    private static final String _ques2 = "\\question-2\\main.csv";
    private static final String _ques3 = "\\question-3\\main.csv";

    public static String getInputFilePath(int f) {
        if (f == 1) {
            return INPUT.concat(_ques1);
        } else if (f == 2) {
            return INPUT.concat(_ques2);
        } else {
            return INPUT.concat(_ques3);
        }
    }

    public static String getOutputPath() {
        return OUTPUT + "\\main.csv";
    }

    public static FileReader getInputFileReader(int f) throws FileNotFoundException {
        if (f == 1) {
            return new FileReader(getInputFilePath(f));
        } else if (f == 2) {
            return new FileReader(getInputFilePath(f));
        } else {
            return new FileReader(getInputFilePath(f));
        }
    }

    public static FileReader getOutputFileReader() throws FileNotFoundException {
        return new FileReader(getOutputPath());
    }

}

class Two {
    private static final int FILE_MARK = 2;
    TreeMap<String, Integer[]> map;

    BufferedReader br;
    int[] action_col;
    String headers[][];

    Two() throws FileNotFoundException {
        action_col = new int[FILE_MARK];
        headers = new String[][] { { "", "min", "max" }, { "occupation", "", "" } };
        map = new TreeMap<>();
        br = new BufferedReader(Data.getInputFileReader(2));

    }

    void setActionColumns(String[] splits) {
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].equals("occupation")) {
                action_col[0] = i;
            } else {
                if (splits[i].equals("age")) {
                    action_col[1] = i;
                }
            }
        }
    }

    void process() throws IOException {
        String line = br.readLine();
        String[] splits = line.split(",");
        setActionColumns(splits);

        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            } else {
                splits = line.split(",");
                apply(splits[action_col[0]], Integer.parseInt(splits[action_col[1]]));
            }
        }
        writeCsv();
    }

    void apply(String occupation, int age) {
        if (map.containsKey(occupation)) {
            Integer[] list = map.get(occupation);
            if (age < list[0]) {
                list[0] = age;
            } else {
                if (age > list[1]) {
                    list[1] = age;
                }
            }
        } else {
            map.put(occupation, new Integer[] { age, age });
        }
    }

    File fileApply() throws IOException {
        File mainCsv = new File(Data.getOutputPath());
        if (mainCsv.exists()) {
            mainCsv.delete();
        }
        mainCsv.createNewFile();
        return mainCsv;
    }

    String getHeadersRec() {
        StringBuilder builder = new StringBuilder();
        for (String header : headers[0]) {
            builder.append(header + ",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n");
        for (String header : headers[1]) {
            builder.append(header + ",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    void writeCsv() throws IOException {
        FileWriter fileWriter = new FileWriter(fileApply());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(getHeadersRec());

        for (Map.Entry<String, Integer[]> entry : map.entrySet()) {
            String rec = entry.getKey() + "," + entry.getValue()[0] + "," + entry.getValue()[1];
            printWriter.println(rec);
            System.out.println(rec);
        }
        fileWriter.flush();
        fileWriter.close();
    }
}

public class App {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Two().process();
    }
}