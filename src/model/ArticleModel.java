package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ArticleModel extends DocumentModel {
        public LinkedHashMap<String, String> getAllAttributes() {
            LinkedHashMap<String, String> attributes = super.getAllAttributes();
            attributes.putAll(new LinkedHashMap<>() {{
                put("author_name", "");
                put("type", "article");
                put("publication_date", "");
            }});
            return attributes;
    }

    public LinkedHashMap<String, ArrayList<String>> select(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = super.select(attributes);
        return filterRows(filterColumns(generalTable));
   }
}
