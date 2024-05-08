package application;

import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.LinkedHashMap;

import controller.AdherantController;
import controller.TableController;
import view.View;

public class App {
    private static TableController getController(String tableName) {
        switch (tableName.toUpperCase()) {
            case "ADHERANT":
                return new AdherantController();
            default:
                return null;
        }
    }

    private static boolean launchControllerCreation(ArrayList<String> commands) {
        TableController controller = getController(commands.get(1));
        return controller.create();
    }

    private static boolean launchControllerDeletion(ArrayList<String> commands) {
        TableController controller = getController(commands.get(1));
        return controller.delete();
    }

    private static boolean launchControllerUpdate(ArrayList<String> commands) {
        TableController controller = getController(commands.get(1));
        return controller.update();
    }

    private static boolean launchControllerSelection(ArrayList<String> commands) {
        TableController controller = getController(commands.get(1));
        if (commands.size() > 2 && commands.get(2).toUpperCase().equals("-ALL")) {
            return controller.selectAll();
        } else {
            return controller.select();
        }
    }

    private static boolean clearScreen() {
        System.out.print("H\033[H\033[2J");
        System.out.flush();
        return true;
    }

    private static boolean interpreteCommand(ArrayList<String> commands) {
        String firstWord = commands.get(0);
        switch (firstWord.toUpperCase()) {
            case "EXIT":
                return false;   
            case "CREATE":
                return launchControllerCreation(commands);  
            case "SELECT":
                return launchControllerSelection(commands);
            case "UPDATE":
                return launchControllerUpdate(commands);
            case "DELETE":
                return launchControllerDeletion(commands);
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

        // this is a test table to test if the View class is working
        // LinkedHashMap<String, ArrayList<String>> table = new LinkedHashMap<>() {{
        //     put("ID", new ArrayList(Arrays.asList("1", "2", "3")));
        //     put("columnB", new ArrayList(Arrays.asList("cellB1", "cellB2", "cellB3")));
        //     put("columnC", new ArrayList(Arrays.asList("cellC1", "cellC2", "cellC3")));
        //     put("columnD", new ArrayList(Arrays.asList("cellD1", "cellD2", "cellD3")));
        //     put("columnE", new ArrayList(Arrays.asList("cellE1", "cellE2", "cellE3")));
        //     put("columnF", new ArrayList(Arrays.asList("cellF1", "cellF2", "cellF3")));
        // }};
        do {
            // display the fake table
            // View.displayTable("test table", table);
            cmd = View.readCommand();
            loop = interpreteCommand(cmd);
        } while (loop);
    }
}
