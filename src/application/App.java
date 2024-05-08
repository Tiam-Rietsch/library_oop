package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

import view.View;

public class App {
    private static Scanner in = new Scanner(System.in);
    private static String command;


    
    public static void main(String[] args) throws Exception {
        String[] list = {"hello", "world"};

        // this is a test table to test if the View class is working
        LinkedHashMap<String, ArrayList<String>> table = new LinkedHashMap<>() {{
            put("ID", new ArrayList(Arrays.asList("1", "2", "3")));
            put("columnB", new ArrayList(Arrays.asList("cellB1", "cellB2", "cellB3")));
            put("columnC", new ArrayList(Arrays.asList("cellC1", "cellC2", "cellC3")));
            put("columnD", new ArrayList(Arrays.asList("cellD1", "cellD2", "cellD3")));
            put("columnE", new ArrayList(Arrays.asList("cellE1", "cellE2", "cellE3")));
            put("columnF", new ArrayList(Arrays.asList("cellF1", "cellF2", "cellF3")));
        }};
        do {
            // display the fake table
            View.displayTable("test table", table);
            ArrayList<String> cmd = View.readCommand();
            for (String c : cmd) {
                System.out.println(c + ".");
            }
        } while (true);
    }
}
