package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import model.AdherantModel;
import model.LoanModel;
import model.TableModel;
import view.View;

public class LoanController extends TableController {
    private LoanModel model;

    public LoanController() {
        this.model = new LoanModel();
    }

    protected TableModel getTableModel() {
        return this.model;
    }

    public boolean returnDocument() {

        String adherantID = View.collectUserInput("adherant_id", "", "");
        String documentID = View.collectUserInput("document_id", "", "");


        // update the database and display new values
        model.returnDocument(adherantID, documentID);
        View.displayTable(model.getTableName(), model.select(null));
        return true;
    }

    public boolean create() {
        LinkedHashMap<String, String> attributes = model.getAllAttributes();
        String value;
        
        // iterate through all the attributes of the model and promt the user for their values
        for (String attr : model.getAllAttributes().sequencedKeySet()) {
            if (!attributes.get(attr).isEmpty()) {
                continue;
            } else if (attr.equals("id")) {
                continue;
            } else if (attr.equals("status")) {
                attributes.put("status", "active");
                continue;
            } else if (attr.equals("lending_date")) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String date = LocalDate.now().format(format).toString();
                attributes.put(attr, date);
                continue;
            } else if (attr.equals("due_date")) {
                value = View.collectCompositeInput(attr, new ArrayList<>(Arrays.asList("day", "month", "year")));
            } else {
                value = View.collectUserInput(attr, "", "");

            }
            
            if (value.equals(".")) {
                break;
            } else  if (value.replaceAll("\\s", "").equals("")) {
                attributes.remove(attr);
            } else  if (attr.equals("adherant_id")) {
                int activeLoanCount = model.getActiveLoanCount(value);

                LinkedHashMap<String, String> id_attribute = new LinkedHashMap<>();
                id_attribute.put("id", value);
                
                LinkedHashMap<String, ArrayList<String>> adherant = new AdherantModel().select(id_attribute);        
                int maxLoanCount = Integer.parseInt(adherant.get("max_loan").get(0));

                if (maxLoanCount == activeLoanCount) {
                    System.out.println("You are not allowed to borrow more book");
                    return true;
                } 
                attributes.put(attr, value);
            } else {
                attributes.put(attr, value);
            }
        }

        // when collected all attributes, insert into database
        model.insert(attributes);
        return true;
    }

    public boolean selectLate() {
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
        View.displayTable(model.getTableName(), model.selectLate(attributes));
        return true;
    }

    public boolean selectActive() {
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
        View.displayTable(model.getTableName(), model.selectActive(attributes));
        return true;
    }

    public boolean selectReturned() {
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
        View.displayTable(model.getTableName(), model.selectReturned(attributes));
        return true;
    }
}
