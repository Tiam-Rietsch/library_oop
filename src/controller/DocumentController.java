package controller;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import model.AdherantModel;
import model.ArticleModel;
import model.BookModel;
import model.DefenseReportModel;
import model.DocumentModel;
import model.MagazineModel;
import model.TableModel;
import view.View;

public class DocumentController extends TableController {
    private TableModel model;
    

    public DocumentController(String tableName) {
        this.model = getModel(tableName);
    }

    protected TableModel getTableModel() {
        return this.model;
    }

    /**
     * This method is used to get the appropriate model class 
     * needed to execute the user command
     * @param tableName
     * @return a TableModel can be either Aherant or Document model
     */
    private static TableModel getModel(String tableName) {
        String documentType = View.collectUserInput("type", "every", "");
        switch (documentType.toUpperCase()) {
            case "BOOK":
                return new BookModel();
            case "ARTICLE":
                return new ArticleModel();
            case "REPORT":
                return new DefenseReportModel();
            case "MAGAZINE":
                return new MagazineModel();
            case "EVERY":
                return new DocumentModel();
            default:
                return null;
        }
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
            } else if (attr.equals("nbr_borrow")) {
                attributes.put(attr, "0");
                continue;
            } else if (attr.equals("copies_left")) {
                attributes.put(attr, attributes.get("nbr_copies"));
                continue;
            } else if (attr.equals("location")) {
                value = View.collectCompositeInput("location", new ArrayList<>(Arrays.asList("hall", "shelf")));
            } else {
                value = View.collectUserInput(attr, "", "");
            }

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
