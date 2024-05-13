package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MagazineModel extends DocumentModel {
    public LinkedHashMap<String, String> getAllAttributes() {
        LinkedHashMap<String, String> attributes = super.getAllAttributes();
        attributes.putAll(new LinkedHashMap<>() {{
            put("type", "magazine");
            put("publishing_frequency", "");
        }});
        return attributes;
    }

    public LinkedHashMap<String, ArrayList<String>> select(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = super.select(attributes);
        return filterRows(filterColumns(generalTable));
   }
}
