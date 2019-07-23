package com.javarush.task.task31.task3105;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/* 
Добавление файла в архив
*/
public class Solution {
    public static void main(String[] args) throws IOException {

        // null checking
        if (args.length == 0) return;
        else if (!args[1].endsWith(".zip")) return;


        // variables
        String resultFile = args[0];
        String fileName = args[0].substring(args[0].lastIndexOf("/") + 1);
        String outputZIP = args[1];
        HashMap<String, byte[]> map = new HashMap<>();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(args[1]));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        // 1. reading to HashMap (to RAM)
        ZipEntry zipEntryInputFile;
        while ((zipEntryInputFile = zis.getNextEntry()) != null) {
            int length;
            byte[] buffer = new byte[1000 * 1024];
            while ((length = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            map.put(zipEntryInputFile.getName(), baos.toByteArray());
            baos.reset();
        }
        zis.close();


        // 2. searching matches (resultFile name)
        boolean matchFound = false;
        for (Map.Entry<String, byte[]> pair : map.entrySet()) {
            String k = pair.getKey();
            if (k.endsWith("/" + fileName)) {
                matchFound = true;
                break;
            }
        }


        // 3.1. match NOT found -> create folder 'new' and add result file to 'new/resultFile'
        if (!matchFound) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZIP));
            // put resultFile to archive
            String folderNew = "new/" + fileName;
            ZipEntry folderNewZipEntry = new ZipEntry(folderNew); //write resultFile to archive 'new/resultFile'
            zos.putNextEntry(folderNewZipEntry);
            Files.copy(Paths.get(resultFile), zos);
            zos.closeEntry();
            // put other files to archive
            for (Map.Entry<String, byte[]> mapEntry : map.entrySet()) {
                String k = mapEntry.getKey();
                byte[] v = mapEntry.getValue();
                ZipEntry zipEntry = new ZipEntry(k);
                zos.putNextEntry(zipEntry);
                if (!zipEntry.isDirectory()) zos.write(v);
                zos.closeEntry();
            }
            zos.close();
        }


        // 3.2. match found -> replace file
        if (matchFound) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZIP));
            for (Map.Entry<String, byte[]> pair : map.entrySet()) {
                String k = pair.getKey();
                byte[] v = pair.getValue();
                ZipEntry zipEntry = new ZipEntry(k);
                zos.putNextEntry(zipEntry);
                // update result file only
                if (k.endsWith("/" + fileName)) Files.copy(Paths.get(resultFile), zos);
                // just write other files
                else if (!zipEntry.isDirectory()) zos.write(v);
                zos.closeEntry();
            }
            zos.close();
        }
    }
}
