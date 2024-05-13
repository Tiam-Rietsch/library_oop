package controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import model.AdherantModel;
import model.TableModel;
import view.View;


public class AdherantController extends TableController {
    private AdherantModel model;

    public AdherantController() {
        this.model = new AdherantModel();
    }

    protected TableModel getTableModel() {
        return this.model;
    }

    public boolean create() {
        LinkedHashMap<String, String> attributes = model.getAllAttributes();
        String value;

        // iterate through all the attributes of the model and promt the user for their values
        for (String attr : model.getAllAttributes().sequencedKeySet()) {
            // retrieve the attributes value (composite, simple or auto generated)
            if (!attributes.get(attr).isEmpty()) {
                continue;
            } else if (attr.equals("id")) {
                continue;
            } else if (attr.equals("max_loan")) {
                String type = attributes.get("type");
                if (type.toUpperCase().equals("VISITOR")) {
                    attributes.put("max_loan", "1");
                } else if (type.toUpperCase().equals("STUDENT")) {
                    attributes.put("max_loan", "2");
                } else if (type.toUpperCase().equals("TEACHER")) {
                    attributes.put("max_loan", "4");
                }
                continue;
            } else if (attr.equals("address")) {
                value = View.collectCompositeInput("address", new ArrayList<>(Arrays.asList("quater", "town", "country")));
            } else {
                value = View.collectUserInput(attr, "", "");
            }
            
            // check if the value can be inserted or has to be removed (in case empty)
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
}
