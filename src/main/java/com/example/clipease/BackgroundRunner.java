package com.example.clipease;

import java.io.File;
import java.io.IOException;

public class BackgroundRunner {

    public static void startInBackground(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File("/path/to/directory/")); // Set the working directory as needed
            pb.redirectErrorStream(true);
            pb.start();
            System.out.println("Application started in the background.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
