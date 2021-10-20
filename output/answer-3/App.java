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

class YCardRec {
    final String recNumn;
    final int yCardCount;
    final String teamName;

    YCardRec(String recNum, int yCardCount, String teamName) {
        this.recNumn = recNum;
        this.yCardCount = yCardCount;
        this.teamName = teamName;
    }

    public static int compLogic(YCardRec ob1, YCardRec ob2) {
        if (ob1.yCardCount < ob2.yCardCount) {
            return 1;
        } else if (ob1.yCardCount > ob2.yCardCount) {
            return -1;
        } else {
            return ob1.teamName.compareTo(ob2.teamName);
        }
    }

    @Override
    public String toString() {
        return recNumn + "\t" + teamName + "\t" + yCardCount + "\t";
    }
}

final class CardArrangementLogic {
    private CardArrangementLogic() {

    }

    static class Red implements Comparator<Integer> {

        @Override
        public int compare(Integer rCount1, Integer rCount2) {
            if (rCount1 < rCount2) {
                return 1;
            } else {
                if (rCount1 > rCount2) {
                    return -1;
                }
            }
            return 0;
        }

    }

    static class Yellow implements Comparator<YCardRec> {

        @Override
        public int compare(YCardRec ob1, YCardRec ob2) {
            return YCardRec.compLogic(ob1, ob2);
        }

    }
}

class Three {
    private static final int FILE_MARK = 3;
    TreeMap<Integer, TreeSet<YCardRec>> rRec;

    BufferedReader br;
    int actionCol[];
    String[] headers;

    Three() throws FileNotFoundException {
        br = new BufferedReader(Data.getInputFileReader(FILE_MARK));
        rRec = new TreeMap<>(new CardArrangementLogic.Red());
        headers = new String[] { "Team", "Yellow Cards", "Red Cards" };
        actionCol = new int[3];
    }

    void setActionColumns(String[] splits) {
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].equals(headers[2])) {
                actionCol[0] = i;
            } else {
                if (splits[i].equals(headers[1])) {
                    actionCol[1] = i;
                } else {
                    if (splits[i].equals(headers[0])) {
                        actionCol[2] = i;
                    }
                }
            }
        }
    }

    void process() throws IOException {
        String line = br.readLine();
        String[] splits = line.split(",");
        setActionColumns(splits);

        int recNum = 0;
        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            } else {
                splits = line.split(",");
                apply(Integer.parseInt(splits[actionCol[0]]), Integer.parseInt(splits[actionCol[1]]),
                        splits[actionCol[2]], Integer.toString(recNum++));
            }
        }
        writeCsv();
    }

    void apply(int rCards, int yCards, String teamName, String recNum) {
        if (rRec.containsKey(rCards)) {
            rRec.get(rCards).add(new YCardRec(recNum, yCards, teamName));
        } else {
            TreeSet<YCardRec> yRec = new TreeSet<>(new CardArrangementLogic.Yellow());
            yRec.add(new YCardRec(recNum, yCards, teamName));
            rRec.put(rCards, yRec);
        }
    }

    File fileApply() throws IOException {
        File mainCsv = new File(Data.getOutputPath());
        if (mainCsv.exists()) {
            System.out.println("file found");
            mainCsv.delete();
        }
        mainCsv.createNewFile();
        return mainCsv;
    }

    String getHeadersRec() {
        return "," + headers[0] + "," + headers[2] + "," + headers[1];
    }

    void writeCsv() throws FileNotFoundException, IOException {
        FileWriter fileWriter = new FileWriter(fileApply());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(getHeadersRec());

        for (Map.Entry<Integer, TreeSet<YCardRec>> entry : rRec.entrySet()) {
            Iterator<YCardRec> iter = entry.getValue().iterator();
            while (iter.hasNext()) {
                YCardRec yCardRec = iter.next();
                String fullRec = yCardRec.recNumn + "," + yCardRec.teamName + "," + entry.getKey() + ","
                        + yCardRec.yCardCount;
                printWriter.println(fullRec);
            }
        }

        fileWriter.flush();
        fileWriter.close();
    }

}

public class App {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Three().process();
    }
}