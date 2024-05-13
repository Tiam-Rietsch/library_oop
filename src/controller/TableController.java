package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;


import model.TableModel;
import view.View;

public abstract class TableController {

    protected abstract TableModel getTableModel();


    public boolean create() {
        TableModel model = getTableModel();
        LinkedHashMap<String, String> attributes = model.getAllAttributes();

        // iterate through all the attributes of the model and promt the user for their values
        for (String attr : model.getAllAttributes().sequencedKeySet()) {
            if (attr.equals("id")) {
                continue;
            } 
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
        TableModel model = getTableModel();
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
        TableModel model = getTableModel();

        // Run the SQL command using hte model and iterat through database
        // attributes are null because query doesnot need arguments
        View.displayTable(model.getTableName(), model.select(null));
        return true;
    }

    public boolean update() {
        TableModel model = getTableModel();

        // this will store the list of all attributes entered by the user
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        // first query the id of the record to update
        String id = View.collectUserInput("id", "", "enter " + model.getTableName() + " ID");
        // get all the default values of the given record fromt the database
        LinkedHashMap<String, ArrayList<String>> defaultValues = model.select(new LinkedHashMap<>() {{put("id", id);}});

        // retrieve new user values
        for (String attr: model.getAllAttributes().sequencedKeySet()) {
            if (attr.equals("id")) continue;
            String value = View.collectUserInput(attr, defaultValues.get(attr).get(0), "");

            if (value.equals(".")) {
                break;
            } else  if (value.replaceAll("\\s", "").equals("")) {
                // if the values is blank then use default one
                attributes.put(attr, defaultValues.get(attr).get(0));
            } else {
                attributes.put(attr, value);
            }            
        }

        // update the database and display new values
        model.update(id, attributes);
        View.displayTable(model.getTableName(), model.select(attributes));
        return true;
    }

    public boolean delete() {
        TableModel model = getTableModel();

        String id = View.collectUserInput("id", "", "");
        model.delete(id);
        View.displayTable(model.getTableName(), model.select(null));
        return true;
    }

}
