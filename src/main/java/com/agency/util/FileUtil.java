package com.agency.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Static utility class responsible for all raw file I/O ops.
 * No other class should directly use BufferedReader or BufferedWriter and all file access flows through this class
 */

public class FileUtil {
    /**Private constructor - prevents instantiation and since its a utility class all methods are static. */
    // private because this class is never instantiated and always called statically
    private FileUtil(){}

    // For reading file from the original file
    public static List<String> readAllLines(String filePath){

        File file = new File(filePath);
        if(!file.exists()){
            return new ArrayList<>();
        }
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
        return lines;
    }

    //For writing to tmp file
    public static void writeAllLines(String filePath, List<String> lines){
        File original = new File(filePath);
        File temp = new File(filePath + ".tmp");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))){
            for(String line : lines){
                writer.write(line);
                writer.newLine();
            }
        } catch(IOException e){
            throw new RuntimeException("Failed to write file: "+ filePath, e);
        }

        original.delete();
        temp.renameTo(original);
    }

    //To add a new record at the end of the file without needing to overwrite the whole file again
    public static void appendLine(String filePath, String line) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))){
            writer.write(line);
            writer.newLine();
        } catch(IOException e){
            throw new RuntimeException("Failed to append to file: "+ filePath, e);
        }
    }

    //To check if file exists, if not create it
    public static void ensureFileExists(String filePath){
        File file = new File(filePath);

        if(file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        if(!file.exists()) {
            try{
                file.createNewFile();
            } catch (IOException e){
                throw new RuntimeException("Failed to create file: " + filePath, e);
            }
        }
    }
}
