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

class One {
    private static final int FILE_MARK = 1;
    TreeMap<Integer, LinkedList<Long>> yearRec;

    BufferedReader br;
    int actionCol[];
    String[] headers;

    One() throws FileNotFoundException {
        br = new BufferedReader(Data.getInputFileReader(FILE_MARK));
        yearRec = new TreeMap<>();
        headers = new String[11];
        actionCol = new int[11];
    }

    void setActionColumns(String[] splits) {
        int index = -1;
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].equals("Total")) {
                continue;
            }
            index++;
            headers[index] = splits[i];
            actionCol[index] = i;
        }
    }

    LinkedList<Long> getStatRec(String[] splits) {
        LinkedList<Long> statRec = new LinkedList<>();
        for (int i = 1; i < actionCol.length; i++) {
            statRec.add(Long.parseLong(splits[actionCol[i]]));
        }
        return statRec;
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
                addRec(Integer.parseInt(splits[0]), getStatRec(splits));
            }
        }
        yearRec = apply();
        System.gc();
        writeCsv();
    }

    void addRec(int year, LinkedList<Long> statRec) {
        yearRec.put(year, statRec);
    }

    void addStats(LinkedList<Long> s1, LinkedList<Long> s2) {
        for (int i = 0; i < s1.size(); i++) {
            s1.set(i, s1.get(i) + s2.get(i));
        }
    }

    TreeMap<Integer, LinkedList<Long>> apply() {
        final int GRP_SIZE = 10;

        int begin = yearRec.firstKey();
        int end = begin + GRP_SIZE;

        TreeMap<Integer, LinkedList<Long>> grouped = new TreeMap<>();

        for (; begin <= yearRec.lastKey(); begin += GRP_SIZE, end = begin + GRP_SIZE) {
            NavigableMap<Integer, LinkedList<Long>> subRec = yearRec.subMap(begin, true, end, false);
            Iterator<Map.Entry<Integer, LinkedList<Long>>> subRecIter = subRec.entrySet().iterator();

            if (subRecIter.hasNext()) {
                LinkedList<Long> ini = subRecIter.next().getValue();
                Map.Entry<Integer, LinkedList<Long>> nextRec;

                while (subRecIter.hasNext()) {
                    nextRec = subRecIter.next();
                    addStats(ini, nextRec.getValue());
                }
            }

            grouped.put(begin, subRec.firstEntry().getValue());

        }

        return grouped;
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
        for (String header : headers) {
            builder.append(header + ",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    void writeCsv() throws IOException {
        FileWriter fileWriter = new FileWriter(fileApply());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println(getHeadersRec());

        for (Map.Entry<Integer, LinkedList<Long>> yRec : yearRec.entrySet()) {
            String fullRec = yRec.getKey().toString();
            for (Long stat : yRec.getValue()) {
                fullRec += "," + stat;
            }
            printWriter.println(fullRec);
        }

        fileWriter.flush();
        fileWriter.close();
    }

}

public class App {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        new One().process();
    }
}