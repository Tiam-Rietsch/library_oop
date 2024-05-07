package application;

import java.util.Scanner;

public class App {
    private static Scanner in = new Scanner(System.in);
    private static String command;
    
    public static void main(String[] args) throws Exception {
        do {
            System.out.print("\n$ ");
            command = in.nextLine();
            if (command.equals("exit")) {
                return;
            }
        } while (true);
    }
}
