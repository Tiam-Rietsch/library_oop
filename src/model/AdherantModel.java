package model;

import java.util.LinkedHashMap;

public class AdherantModel extends TableModel {
    public LinkedHashMap<String, String> getAllAttributes() {
        return new LinkedHashMap<String, String>() {{
            put("id", "");
            put("name", "");
            put("surename", "");
            put("address", "");
            put("type", "");
            put("max_loan", "");
        }};
    }

    public String getTableName() {
        return "Adherant";
    }
}
