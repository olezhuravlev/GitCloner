package org.gitcloner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private static final String COMMAND = "git clone %s";
    private static final String SUCCESS_MARK = "+";
    private static final String FAIL_MARK = "";


    public static void main(String[] args) {

        String sourcePath;
        String destinationPath;

        if (args.length == 0) {
            throw new IllegalArgumentException("Please, specify source text file and destination directory!");
        } else if (args.length == 1) {
            sourcePath = args[0];
            destinationPath = "";
        } else {
            sourcePath = args[0];
            destinationPath = args[1];
        }

        System.out.printf("===> Run command with arguments:%n\t- sourcePath: %s;%n\t- destinationPath: %s%n", sourcePath, destinationPath);

        try {
            readFile(sourcePath, destinationPath);
        } catch (IOException e) {
            System.out.println("===> File reading error: " + e.getLocalizedMessage());
        }
    }

    private static void readFile(String sourcePath, String destinationPath) throws IOException {

        byte[] successMarkBytes = SUCCESS_MARK.getBytes(StandardCharsets.UTF_8);
        byte[] failMarkBytes = FAIL_MARK.getBytes(StandardCharsets.UTF_8);
        byte[] newLineBytes = {10};

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(sourcePath, "rws")) {
            String row;
            long prevFilePointer = 0;
            while ((row = randomAccessFile.readLine()) != null) {

                if (row.trim().isBlank()) continue;

                int result = invokeCommand(row, destinationPath);
                byte[] resultMarkBytes;
                if (result == 0) {
                    resultMarkBytes = successMarkBytes;
                } else {
                    resultMarkBytes = failMarkBytes;
                }
                byte[] rowBytes = row.getBytes(StandardCharsets.UTF_8);
                byte[] modifiedRow = new byte[rowBytes.length + newLineBytes.length];
                System.arraycopy(rowBytes, 0, modifiedRow, 0, rowBytes.length);
                System.arraycopy(resultMarkBytes, 0, modifiedRow, 0, resultMarkBytes.length);
                System.arraycopy(newLineBytes, 0, modifiedRow, rowBytes.length, newLineBytes.length);
                long filePointer = randomAccessFile.getFilePointer();
                randomAccessFile.seek(prevFilePointer);
                randomAccessFile.write(modifiedRow);
                prevFilePointer = filePointer;
            }
        }
    }

    private static int invokeCommand(String argument, String destinationPath) {
        try {

            String command = String.format(COMMAND, argument);

            List<String> commands = new ArrayList<>(Arrays.asList(command.split(" ")));

            ProcessBuilder builder = new ProcessBuilder(commands);
            if (!destinationPath.isBlank()) {
                builder.directory(new File(destinationPath));
            }
            builder.redirectErrorStream(true);
            Process process = builder.start();

            InputStream stdout = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("===> " + line);
            }

            return process.waitFor();

        } catch (Exception e) {
            System.out.println("===> Command invocation error: " + e.getLocalizedMessage());
            return 1;
        }
    }
}
