package vladislavmaltsev.xmltojsontask.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.XML;
import org.springframework.stereotype.Service;
import vladislavmaltsev.xmltojsontask.entity.JsonData;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonDataService {
    public static final String NUMBER_OF_RECORDS_LINE = "Number of records: %d";
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return !file.exists() || file.length() == 0;
    }

    public String saveIntoLog(String xmlData) {
        String json = XML.toJSONObject(xmlData).toString();
        JsonData jsonData;
        try {
            jsonData = new ObjectMapper().readValue(json, JsonData.class);
        } catch (JsonProcessingException e) {throw new RuntimeException(e);}
        String pathToLogFile = pathDefinition(jsonData);
        if (isFileEmpty(pathToLogFile)) writeInEmptyFile(pathToLogFile, json);
        else {
            try (
                    var bufferedReader = new BufferedReader(new FileReader(pathToLogFile));
                    var bufferedWriter = new BufferedWriter(new FileWriter(pathToLogFile + ".tmp"))
            ) {
                copyFileWithNewCount(bufferedReader, bufferedWriter, pathToLogFile);
                bufferedWriter.write(json);
                bufferedWriter.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new File(pathToLogFile).delete();
            new File(pathToLogFile + ".tmp").renameTo(new File(pathToLogFile));
        }
        return json;
    }

    public void copyFileWithNewCount(
            BufferedReader bufferedReader,
            BufferedWriter bufferedWriter,
            String path) throws IOException {
        String line;
        Integer recordsNumber = readOldRecordsNumberLine(path);
        String oldLine = String.format(NUMBER_OF_RECORDS_LINE, recordsNumber);
        String newLine = String.format(NUMBER_OF_RECORDS_LINE, ++recordsNumber);
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
            bufferedWriter.newLine();
            bufferedWriter.write(json);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer readOldRecordsNumberLine(String path) {
        Pattern firstLinePattern = Pattern.compile("Number of records: (\\d+)");
        try (var bufferedWriter = new BufferedReader(new FileReader(path))) {
            Matcher matcher = firstLinePattern.matcher(bufferedWriter.readLine());
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
