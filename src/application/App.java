package application;

import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.LinkedHashMap;

import controller.AdherantController;
import controller.TableController;
import controller.LoanController;
import controller.DocumentController;
import view.View;

public class App {

    /**
     * returns the controller of the corresponding the the input command
     */
    private static TableController getController(String tableName) {
        switch (tableName.toUpperCase()) {
            case "ADHERANT":
                return new AdherantController();
            case "DOCUMENT":
                return new DocumentController(tableName);
            case "LOAN":
                return new LoanController();
            default:
                return null;
        }
    }

    /**
     * This method is used to launch the controller creation method
     * @param commands this list of commands useful because we still need the name of the
     *                 controller to be used
     * @return  return true if executed properly, otherwise return false
     */
    private static boolean launchControllerCreation(ArrayList<String> commands) {
        TableController controller  = getController(commands.get(1));
        return controller.create();
    }

    /**
     * This is method is used to launch the controllers deletion method 
     * @param commands we need the list off command because we need to extract
     *                 the required controler name
     * @return true if executed properly otherwise return false
     */
    private static boolean launchControllerDeletion(ArrayList<String> commands) {
        TableController controller  = getController(commands.get(1));
        return controller.delete();
    }

    /**
     * This is method is used to launch the controllers update method 
     * @param commands we need the list off command because we need to extract
     *                 the required controler name
     * @return true if executed properly otherwise return false
     */
     private static boolean launchControllerUpdate(ArrayList<String> commands) {
        TableController controller  = getController(commands.get(1));
        return controller.update();
    }

    private static boolean launchControllerReturn(ArrayList<String> commands) {
        if (commands.size() > 1 && commands.get(1).toUpperCase().equals("DOCUMENT")) {
            LoanController controller = new LoanController();
            return controller.returnDocument();
        }
        return false;
    }

    /**
     * This is method is used to launch the controllers selection method 
     * @param commands we need the list off command because we need to extract
     *                 the required controler name
     * @return true if executed properly otherwise return false
     */
    private static boolean launchControllerSelection(ArrayList<String> commands) {
        TableController controller  = getController(commands.get(1));

        // launch special controllers depending the the end arguments
        if (commands.size() > 2 && commands.getLast().toUpperCase().equals("-ALL")) {
            return controller.selectAll();
        } else if (commands.size() > 2 && commands.getLast().toUpperCase().equals("-LATE")) {
            System.out.println("you want late");
            return ((LoanController) controller).selectLate();
        } else if (commands.size() > 2 && commands.getLast().toUpperCase().equals("-ACTIVE")) {
            return ((LoanController) controller).selectActive();
        } else if (commands.size() > 2 && commands.getLast().toUpperCase().equals("-RETURNED")) {
            return ((LoanController) controller).selectReturned();
        } else {
            return controller.select();
        }
    }

    /**
     * This method whipes out the entire console
     * @return true if executed properly otherwise return false
     */
    private static boolean clearScreen() {
        System.out.print("H\033[H\033[2J");
        System.out.flush();
        return true;
    }

    /**
     * Interprets the command by analysing the various arguments and
     * call the adequate controller
     * @param commands  array of Strings making up the entire user command
     * @return true if command execution was completed properly and false otherwise
     */
    private static boolean interpreteCommand(ArrayList<String> commands) {
        String firstWord = commands.get(0);
        switch (firstWord.toUpperCase()) {
            case "EXIT":
                return false;   
            case "ADD":
                return launchControllerCreation(commands);  
            case "GET":
                return launchControllerSelection(commands);
            case "EDIT":
                return launchControllerUpdate(commands);
            case "REMOVE":
                return launchControllerDeletion(commands);
            case "RETURN":
                return launchControllerReturn(commands);
            case "CLEAR":
                return clearScreen();
            default:
                break;
        }
        return true;
    }
    
    public static void main(String[] args) throws Exception {
        boolean loop = true;
        ArrayList<String> cmd;

        do {
            cmd = View.readCommand();
            loop = interpreteCommand(cmd);
        } while (loop);
    }
}
