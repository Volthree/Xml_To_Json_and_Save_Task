package vladislavmaltsev.xmltojsontask.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;

@Service
public class FileReaderServiceCopy {
    private static final int MAX_RECORDS_PER_FILE = 5;
    private static final String ROOT_SOURCE_FILE_DIRECTORY = "logs/";
    private static final String ROOT_RESULT_DATA_FILE_DIRECTORY = "logs/metadata/";
    int fileNumber = 1;
    int lineNumber = 1;
    long offset = 0;
    String fileToCopyPath;
    boolean readed = false;

    public void readData(String fileName) {
        String fullSourceFilePath = String.format(ROOT_SOURCE_FILE_DIRECTORY + "%s" + ".log", fileName);
        String fullMetaDataPath = defineMetaDataPath(fileName);

        while(!readed){
            System.out.println("Enter main while");
            defineMetadata(fullMetaDataPath);
            defineFileToCopyPath(fileName);
            try (var reader = new BufferedReader(new FileReader(fullSourceFilePath));
                 var writer = new BufferedWriter(new FileWriter(fileToCopyPath, true));
                 var positionMetaFile = new RandomAccessFile(fullMetaDataPath, "rw"))
            {
                String line;
                reader.skip(offset);
                 while ((line = reader.readLine()) != null) {
                    System.out.println("Enter read line");
                    if(!line.equals("")) {
                        if(offset == 0) offset += line.getBytes().length + System.lineSeparator().getBytes().length;
                        else {
                            offset += line.getBytes().length + System.lineSeparator().getBytes().length;
                            if (lineNumber < MAX_RECORDS_PER_FILE) {
                                System.out.println("Enter true with fileNumber = " + fileNumber+" lineNumber "+lineNumber+" offset "+ offset);
                                positionMetaFile.writeInt(fileNumber);
                                positionMetaFile.writeInt(lineNumber++);
                                positionMetaFile.writeLong(offset);
                                writer.write(line);
                                writer.newLine();
                            } else {
                                System.out.println("Enter else with fileNumber = " + fileNumber+" lineNumber "+lineNumber+" offset "+ offset);
                                positionMetaFile.writeInt(fileNumber);
                                positionMetaFile.writeInt(lineNumber);
                                positionMetaFile.writeLong(offset);
                                writer.write(line);
                                System.out.println("Before break");
                                Thread.sleep(100);
                                break;
                            }
                        }
                    }}
                if(!reader.ready()){
                    System.out.println("Равно нал");
                    break;}
                System.out.println("End try with resources");
            } catch (IOException | InterruptedException e) {throw new RuntimeException(e);}
        }
    }
    private void defineFileToCopyPath(String fileName) {
        String currentPathDirectory = String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s" + "/",
                fileName);
        new File(currentPathDirectory).mkdirs();
        fileToCopyPath = String.format(
                currentPathDirectory + "%s" + "-" + "%s" + ".log",
                fileName,
                formatNumber(fileNumber));
    }
    public void defineMetadata(String fullMetaDataPath){
        try (RandomAccessFile metaFile = new RandomAccessFile(fullMetaDataPath, "rw"))
        {
            if (metaFile.length() >= 16) {
                metaFile.seek(metaFile.length() - 16);
                fileNumber = metaFile.readInt();
                metaFile.seek(metaFile.length() - 12);
                lineNumber = metaFile.readInt();
                metaFile.seek(metaFile.length() - 8);
                offset = metaFile.readLong();
                System.out.println(fileNumber + " " + lineNumber + " " + offset);
            }
            if (lineNumber >= MAX_RECORDS_PER_FILE) {
                fileNumber++;
                lineNumber = 1;
                System.out.println("lineNumber >= MAX: write to meta filenumber " + fileNumber + " linenumber " + lineNumber);
            }
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    private String defineMetaDataPath(String fileName) {
        return String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s" + "/" + "%s" + "-meta.log",
                fileName,
                fileName);
    }
    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        return decimalFormat.format(number);
    }
}
