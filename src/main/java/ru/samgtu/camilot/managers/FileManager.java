package ru.samgtu.camilot.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {

    public static boolean isFileExist(String filePath) {
        if (filePath == null) return false;
        File file = new File(filePath);
        return file.exists();
    }

    public static List<String> readList(String filePath) {
        List<String> list = new ArrayList<>();
        File file = new File(filePath);

        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    list.add(scanner.nextLine());
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else System.out.println("Не найден файл: " + filePath);
        return list;
    }

    public static void saveList(List<String> data, String filePath) {
        try {
            PrintWriter out = new PrintWriter(filePath);
            for (String datum : data) {
                out.println(datum);
            }
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("saveFile: An error occurred while saving file: " + filePath);
            e.printStackTrace();
        }
    }

    public static File[] getFilesInDir(String pathName) {
        File dir = new File(pathName);
        return dir.listFiles();
    }
}
