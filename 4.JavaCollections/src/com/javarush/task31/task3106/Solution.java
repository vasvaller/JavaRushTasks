package com.javarush.task.task31.task3106;

import sun.misc.IOUtils;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/*
Разархивируем файл
*/
public class Solution {
    public static void main(String[] args) throws IOException {

        // variables declaration
        String resultFile;
        ArrayList<FileInputStream> inputStreamsList = new ArrayList<FileInputStream>();

        // 1. checking args[0] for null
        // 2. sorting args from [1] to [end] <IMPORTANT>
        // 3. adding args to inputStreamsList
        if (args[0] == null) return;
        else {
            Arrays.sort(args, 1, args.length);
            resultFile = args[0];
            for (int i = 1; i < args.length; i++) {
                inputStreamsList.add(new FileInputStream(args[i]));
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(resultFile);

        // creating enumeration from list
        Enumeration<FileInputStream> enumeration = Collections.enumeration(inputStreamsList);

        // creating ZipInputStream and use SequenceInputStream as queue of inputStreams
        ZipInputStream zipInputStream = new ZipInputStream(new SequenceInputStream(enumeration));

        // just create it for validation system but not necessary for real work
        // cause we have only one file with concrete name
        // * As usual we use zipEntry when it necessary to get Name, Attributes, Size etc of zipped file
        ZipEntry entry = zipInputStream.getNextEntry();

        // reading zipInputStream stream fully in single iteration
        // * single string read operation, but only for small files
        byte[] bytes = IOUtils.readFully(zipInputStream, Integer.MAX_VALUE, true);

        // write all readed data to resultFile in single iteration
        fileOutputStream.write(bytes);

        // close streams
        fileOutputStream.flush();
        fileOutputStream.close();
        zipInputStream.close();
    }
}