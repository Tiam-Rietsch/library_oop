package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BookModel extends DocumentModel {

    public LinkedHashMap<String, String> getAllAttributes() {
        LinkedHashMap<String, String> attributes = super.getAllAttributes();
        attributes.putAll(new LinkedHashMap<>() {{
            put("author_name", "");
            put("editor_name", "");
            put("editing_date", "");
            put("type", "book");
        }});
        return attributes;
    }

    public LinkedHashMap<String, ArrayList<String>> select(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = super.select(attributes);
        return filterRows(filterColumns(generalTable));
   }
}
