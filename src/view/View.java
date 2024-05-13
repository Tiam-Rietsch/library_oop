package view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class View {
    private static Scanner scanner = new Scanner(System.in);

    /**
     * This method displays the table header row
     * @param table
     */
    private static void drawTableHeader(HashMap<String, ArrayList<String>> table) {
    	if (table.isEmpty()) {
    		System.out.print("the table is empty");
    		return;
    	}
        // first display the row boundary ("|")
        System.out.print("|");
    
        for (String header : table.keySet()) {
            ArrayList<String> column = table.get(header);

            // print out a header with the specific cell formating
            System.out.printf(createFormatString(header, column), header);
        }
    }

    /**
     * This method draws out the rows of the table 
     * @param table
     */
    private static void drawTableRows(HashMap<String, ArrayList<String>> table) {
        int i = 0;
        do {
            // first get the set of table headers and check if the boundary has exceeded
            Set<String> headers = table.keySet();
            if (i >= table.get(headers.toArray()[0]).size()) break;

            // start printing the raw by first printing a "|" border
            System.out.print("|");
            // iterate through every column and print the cell of row index i
            for (String header : headers) {
                ArrayList<String> column = table.get(header);
                String format = createFormatString(header, column);

                // print out a cell with the specific column format
                System.out.printf(format, column.get(i)); 
            }   
            // return to next line after printing row
            System.out.println("");

            i++; 
        } while(i > 0);
    }

    /**
     * This method compares the string lenght of all the cells in a calumn to get the
     * best fitting width
     * 
     * @param header because we have to include the column header width in the comparison
     * @param column this is an array list of all the cell values in the column
     * @return the minimum width that fits all the cells in column
     */
    private static int getColumnWidth(String header, ArrayList<String> column) {
        int width = header.length();
        for (String cellValue : column) {
            width = cellValue.length() > width ? cellValue.length() : width;
        }
        return width;
    }

    /**
     * this method creates a formating string to format the cell value according to the 
     * space needed for the column
     * 
     * @param header because the method exploits getColumnWidth, we have to insert it there
     * @param column because the method exploits getColumnWidth, we have to insert it there
     * @return a formated string for the column eg. | %-10s | (10 reserved spaces for a string)
     *         note that number of reserved spaces is calculated using getColumnWidth method
     */
    private static String createFormatString(String header, ArrayList<String> column) {
        String format = "";

        format += " %-" + Integer.toString(getColumnWidth(header, column)) + "s |";
        return format;
    }

    /**
     * This method draws horizontal line by combining "+" and "-" to fit the table
     * width
     * @param table we pass in the full table on which we want to draw a horizontal line width
     * @return return a line that fits the table width
     */
    private static String drawLineWidth(HashMap<String, ArrayList<String>> table) {
        String line = "";
        for (String header : table.keySet()) {
            line += "+--";
            ArrayList<String> column = table.get(header);
            for (int i = 0; i < getColumnWidth(header, column); i++) {
                line += "-";
            }
        }
        return line += "+";
    }

    private static ArrayList<String> cleanCommand(ArrayList<String> command) {
        ArrayList<String> finalCmd = new ArrayList<>();
        for (String arg : command) {
            if (!arg.isEmpty()) finalCmd.add(arg);
        }
        return finalCmd;
    }

    /**
     * this method can be used to display a fully formated table on screen.
     * it makes use of the private utility methods to print out header, rows and lines
     * @see drawTableHeader, drawTableRows, drawLineWidth
     * @param title the title of the table
     * @param table the table hashmap
     */
    public static void displayTable(String title, HashMap<String, ArrayList<String>> table) {
    	if (table.isEmpty()) {
    		System.out.print("the table is empty");
    		return;
    	}
        String line = drawLineWidth(table);
        System.out.printf("%s \n", title.toUpperCase());

        // draw table header
        System.out.printf("%s \n", line);
        drawTableHeader(table);
        System.out.printf("\n%s \n", line);

        // draw rows
        drawTableRows(table);
        System.out.printf("%s \n", line);
    }

    public static void displayError(String error) {
        String RED = "\u001B[31m";
        String RESET = "\u001B[0m";

        System.out.println("\t" + RED + error + RESET);
    }

    /**
     * This method is used to retrieve user input on console
     * @param inputName the name of the field to input
     * @param defaultValue the default value of the field in database
     * @param helperText text that indicates the user  on what to do
     * @return returns a string representing the user's input
     */
    public static String collectUserInput(String inputName, String defaultValue, String helperText) {
        String output = "\t# " + inputName;
        String input = "";

        output += defaultValue.equals("") ? "" : " ~ " + defaultValue;
        output += helperText.equals("") ? "" : "(" + helperText + ")";
        System.out.print(output + ": ");

        input = scanner.nextLine();
        return input.isBlank() ? defaultValue : input;
    }

    public static String collectCompositeInput(String title, ArrayList<String> components) {
        String input = "";

        System.out.print("\t" + title + "\n");
        for (String c : components) {
            System.out.print("\t\t> " + c + ": ");
            String val = scanner.nextLine();
            input += components.getLast().equals(c) ? val : val + "/";
        }

        return input;
    }

    public static ArrayList<String> readCommand() {
        String input;
        ArrayList<String> cmd;

        // get input from screen and split command argument into an array
        System.out.print("\n$ ");
        input = scanner.nextLine();
        cmd = new ArrayList<>(Arrays.asList(input.split(" ")));

        // returns the cleaned array by eliminating white spaces
        return cleanCommand(cmd);
    }

}
