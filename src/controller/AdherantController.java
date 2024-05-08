package controller;

import java.util.LinkedHashMap;

import model.AdherantModel;
import view.View;

public class AdherantController implements TableController {
    private AdherantModel model;

    public AdherantController() {
        this.model = new AdherantModel();
    }


    public boolean create() {
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        // iterate through all the attributes of the model and promt the user for their values
        for (String attr: model.getAllAttributes().sequencedKeySet()) {
            if (attr.equals("id")) continue;
            String value = View.collectUserInput(attr, "", "");

            if (value.equals(".")) {
                break;
            } else  if (value.replaceAll("\\s", "").equals("")) {
                attributes.remove(attr);
            } else {
                attributes.put(attr, value);
            }
        }

        // when collected all attributes, insert into database
        model.insert(attributes);
        return true;
    }

    public boolean select() {
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        // iterate through all the attributes of the model and prompt the user for their values
        for (String attr: model.getAllAttributes().sequencedKeySet()) {
            String value = View.collectUserInput(attr, "", "");

            if (value.equals(".")) {
                break;
            } else  if (value.replaceAll("\\s", "").equals("")) {
                attributes.remove(attr);
            } else {
                attributes.put(attr, value);
            }
        }

        // Run the SQL command using the model and iterate through the database
        View.displayTable(model.getTableName(), model.select(attributes));
        return true;
    }

    public boolean selectAll() {
        // Run the SQL command using hte model and iterat through database
        // attributes are null because query doesnot need arguments
        View.displayTable(model.getTableName(), model.select(null));
        return true;
    }

    public boolean update() {
        System.out.println("You want to update an adherant!");
        return true;
    }

    public boolean delete() {
        System.out.println("You want to delete an adherant!");
        return true;
    }
}
