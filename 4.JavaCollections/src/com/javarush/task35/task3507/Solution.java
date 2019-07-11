package com.javarush.task.task35.task3507;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/* 
ClassLoader - что это такое?
*/
public class Solution {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Set<? extends Animal> allAnimals = getAllAnimals(Solution.class.getProtectionDomain().getCodeSource().getLocation().getPath() + Solution.class.getPackage().getName().replaceAll("[.]", "/") + "/data");
        System.out.println(allAnimals);
    }

    public static Set<? extends Animal> getAllAnimals(String pathToAnimals) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        // create Set of Animal objects
        Set<Animal> set = new HashSet<>();

        // get array of files to read
        File dir = new File(pathToAnimals);
        File[] files = dir.listFiles();

        // create object of my own ClassLoader
        MyClassLoader loader = new MyClassLoader();

        // read every file of dir and looking for Animal implementarion class
        // which has default public constructor
        for (int i = 0; i < files.length; i++) {

            // try to load current file and define class
            Class clazz = loader.loadClass(files[i].toPath());

            // try to get public default constructor
            Constructor constructor = null;
            try {
                constructor = clazz.getConstructor();
            } catch (NoSuchMethodException e) {
            }

            // check is class extends Animal and has default constructor
            // if true => creating new instance
            if (Animal.class.isAssignableFrom(clazz)&& constructor != null) {
                set.add((Animal) clazz.newInstance());
            }
        }
        return set;
    }

    // My own ClassLoader
    public static class MyClassLoader extends ClassLoader {

        // loadСlass override
        public Class<?> loadClass(Path fileName) throws ClassNotFoundException, IOException {
            byte[] buffer = Files.readAllBytes(fileName);
            return defineClass(null, buffer, 0, buffer.length);
        }
    }
}
