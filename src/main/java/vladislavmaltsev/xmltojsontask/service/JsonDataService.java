package vladislavmaltsev.xmltojsontask.service;

import org.springframework.stereotype.Service;
import vladislavmaltsev.xmltojsontask.entity.JsonData;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonDataService implements Serializable {

    public static final String baseLine = "Number of records: %d";
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return !file.exists() || file.length() == 0;
    }

    public void saveIntoLog(JsonData jsonData, String json) {

        String path = pathDefinition(jsonData);
        System.out.println(path);

        if (isFileEmpty(path)) {
            writeInEmptyFile(path, json);
        } else {
            try (
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + ".tmp"))
            ) {
                copyFileWithNewCount(bufferedReader, bufferedWriter, path);
                bufferedWriter.write(json);
                bufferedWriter.write(System.lineSeparator());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new File(path).delete();
            new File(path + ".tmp").renameTo(new File(path));
        }
    }

    public void copyFileWithNewCount(
            BufferedReader bufferedReader,
            BufferedWriter bufferedWriter,
            String path) throws IOException {
        String line;
        Integer recordsNumber = readOldRecordsNumberLine(path);
        String oldLine = String.format(baseLine, recordsNumber);
        String newLine = String.format(baseLine, ++recordsNumber);
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(oldLine)) {
                bufferedWriter.write(newLine);
            } else {
                bufferedWriter.write(line);
            }
            bufferedWriter.newLine();
        }
    }

    public void writeInEmptyFile(String path, String json) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
            bufferedWriter.write("Number of records: " + 1);
            bufferedWriter.write(System.lineSeparator());
            bufferedWriter.write(json);
            bufferedWriter.write(System.lineSeparator());
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer readOldRecordsNumberLine(String path) {
        Pattern pattern = Pattern.compile("Number of records: (\\d+)");
        try (BufferedReader bufferedWriter = new BufferedReader(new FileReader(path))) {
            Matcher matcher = pattern.matcher(bufferedWriter.readLine());
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String pathDefinition(JsonData jsonData) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(
                jsonData.getData().getProcess().getStart().getDate(),
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return String.format("logs/%s-%s.log",
                jsonData.getData().getType(),
                zonedDateTime.toLocalDate());
    }
}
