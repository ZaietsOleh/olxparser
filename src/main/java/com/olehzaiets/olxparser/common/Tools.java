package com.olehzaiets.olxparser.common;

public class Tools {

    public interface Task {
        void run() throws Exception;
    }

    public static void safeRun(String error, Task task) {
        try {
            task.run();
        } catch (Exception e) {
            System.out.println(error);
        }
    }
}
