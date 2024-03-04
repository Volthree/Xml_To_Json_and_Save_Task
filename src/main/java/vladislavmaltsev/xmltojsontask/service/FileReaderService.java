package vladislavmaltsev.xmltojsontask.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;

@Service
public class FileReaderService {
    private static final int MAX_RECORDS_PER_FILE = 5;
    private static final String ROOT_SOURCE_FILE_DIRECTORY = "logs/";
    private static final String ROOT_RESULT_DATA_FILE_DIRECTORY = "logs/metadata/";
    private final static int intSizeInBytes = 4;
    private final static int longSizeInBytes = 8;
    int fileNumber;
    int lineNumber;
    long offset;
    String fileToCopyPath;
    boolean isRead;

    {
        fileNumber = 1;
        lineNumber = 1;
        offset = 0;
        isRead = false;
    }

    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        return decimalFormat.format(number);
    }

    public void readData(String fileName) {
        String fullSourceFilePath = String.format(ROOT_SOURCE_FILE_DIRECTORY + "%s" + ".log", fileName);
        String fullMetaDataPath = defineMetaDataPath(fileName);
        while (!isRead) {
            defineMetadata(fullMetaDataPath);
            defineFileToCopyPath(fileName);
            try (var readerFromSource = new BufferedReader(new FileReader(fullSourceFilePath));
                 var writerIntoFiles = new BufferedWriter(new FileWriter(fileToCopyPath, true));
                 var metaFileAccess = new RandomAccessFile(fullMetaDataPath, "rw")) {
                String readedLine;
                readerFromSource.skip(offset);
                while ((readedLine = readerFromSource.readLine()) != null) {
                    if (!readedLine.equals("")) {
                        long indentation = readedLine.getBytes().length + System.lineSeparator().getBytes().length;
                        if (offset == 0) {
                            offset = indentation;
                        } else {
                            offset += indentation;
                            metaFileAccess.writeInt(fileNumber);
                            metaFileAccess.writeInt(lineNumber++);
                            metaFileAccess.writeLong(offset);
                            writerIntoFiles.write(readedLine);
                            if (lineNumber < MAX_RECORDS_PER_FILE) {
                                lineNumber++;
                                writerIntoFiles.newLine();
                            } else break;
                        }
                    }
                }
                if (!readerFromSource.ready()) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void defineFileToCopyPath(String fileName) {
        String currentPathDirectory = String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s/",
                fileName);
        new File(currentPathDirectory).mkdirs();
        fileToCopyPath = String.format(
                currentPathDirectory + "%s-%s.log",
                fileName,
                formatNumber(fileNumber));
    }
    public void defineMetadata(String fullMetaDataPath) {
        try (RandomAccessFile metaFile = new RandomAccessFile(fullMetaDataPath, "rw")) {
            int recordByteLength = longSizeInBytes + intSizeInBytes * 2;
            if (metaFile.length() >= recordByteLength) {
                metaFile.seek(metaFile.length() - recordByteLength);
                fileNumber = metaFile.readInt();
                lineNumber = metaFile.readInt();
                offset = metaFile.readLong();
            }
            if (lineNumber >= MAX_RECORDS_PER_FILE) {
                fileNumber++;
                lineNumber = 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String defineMetaDataPath(String fileName) {
        String currentPathDirectory = String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s/",
                fileName);
        new File(currentPathDirectory).mkdirs();
        return String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s/%s-meta.log",
                fileName,
                fileName);
    }
}
