package vladislavmaltsev.xmltojsontask.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DecimalFormat;

@Service
public class FileReaderService {
    private static final int MAX_RECORDS_PER_FILE = 10;
    private static final String ROOT_SOURCE_FILE_DIRECTORY = "logs/";
    private static final String ROOT_RESULT_DATA_FILE_DIRECTORY = "logs/metadata/";


    public static String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        return decimalFormat.format(number);
    }

    public void readData(String fileName) {
        String fullSourceFilePath = String.format(ROOT_SOURCE_FILE_DIRECTORY + "%s" + ".log", fileName);
        String fullMetaDataPath = defineMetaDataPath(fileName);
        String fileToCopyPath;

        int fileNumber = 1;
        int linenumber = 1;
        long offset = 0;
        try (RandomAccessFile metaFile = new RandomAccessFile(fullMetaDataPath, "rw")) {
            try {
                if (metaFile.length() >= 16) {
                    metaFile.seek(metaFile.length() - 16);
                    fileNumber = metaFile.readInt();
                    metaFile.seek(metaFile.length() - 12);
                    linenumber = metaFile.readInt();
                    metaFile.seek(metaFile.length() - 8);
                    offset = metaFile.readLong()
//                            - System.lineSeparator().getBytes().length
                    ;
                    System.out.println("data in meta filenumber " + fileNumber + " linenumber " + linenumber);
                    System.out.println("offset " + offset);
                }
                if (linenumber >= MAX_RECORDS_PER_FILE) {
                    fileNumber++;
                    linenumber = 1;
                    System.out.println("lineNumber >= MAX: write to meta filenumber " + fileNumber + " linenumber " + linenumber);
                }
                fileToCopyPath = defineFileToCopyPath(fileName, fileNumber);
            } catch (EOFException e) {
                System.out.println("EXCEPTIONNNNNNNNN");
                fileToCopyPath = defineFileToCopyPath(fileName, 1);
            }
            System.out.println("FileToCopePath " + fileToCopyPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean overFlow = false;
        try (
                BufferedReader reader = new BufferedReader(new FileReader(fullSourceFilePath));
                RandomAccessFile positionMetaFile = new RandomAccessFile(fullMetaDataPath, "rw");
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToCopyPath, true))
        ) {
            String line;
            long position = offset;
            reader.skip(offset);
            while ((line = reader.readLine()) != null) {
                if(!line.equals("")) {
                    if (position == 0) {
                        position = position + line.getBytes().length + System.lineSeparator().getBytes().length;
                    } else {
                        position = position + line.getBytes().length + System.lineSeparator().getBytes().length;

                        if (linenumber < MAX_RECORDS_PER_FILE) {
                            System.out.println("Linenumber true " + linenumber);
                            positionMetaFile.writeInt(fileNumber);
                            positionMetaFile.writeInt(linenumber++);
                            positionMetaFile.writeLong(position);
                            writer.write(line);
                            System.out.println("Write line " + line + " in file");
                            writer.newLine();
                        } else {
                            System.out.println("Linenumber false " + linenumber +" and fileNumber "+ fileNumber);
                            System.out.println("Write line " + line + " in file");
                            positionMetaFile.writeInt(fileNumber);
                            positionMetaFile.writeInt(linenumber);
                            positionMetaFile.writeLong(position);
                            writer.write(line);
//                            writer.newLine();
                            overFlow = true;
                            break;
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(overFlow) {
            this.readData(fileName);
        }
    }


    private String defineFileToCopyPath(String fileName, int fileNumber) {
        String currentPathDirectory = String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s" + "/",
                fileName);
        new File(currentPathDirectory).mkdirs();
        return String.format(
                currentPathDirectory + "%s" + "-" + "%s" + ".log",
                fileName,
                formatNumber(fileNumber));
    }

    private String defineMetaDataPath(String fileName) {
        return String.format(
                ROOT_RESULT_DATA_FILE_DIRECTORY + "%s" + "/" + "%s" + "-meta.log",
                fileName,
                fileName);
    }
}
