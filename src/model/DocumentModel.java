package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class DocumentModel extends TableModel {

    /**
     * This method removes all the columns not found the the given table model
     * @param table 
     * @return
     */
    protected LinkedHashMap<String, ArrayList<String>> filterColumns(LinkedHashMap<String, ArrayList<String>> table) {
        LinkedHashMap<String, ArrayList<String>> filteredTable = new LinkedHashMap<>();
        Set<String> currentAttributes = this.getAllAttributes().keySet();

        // if the given column header is not found in the table attributes, do not add the column
        for (String key : table.keySet()) {
            if (currentAttributes.contains(key)) {
                filteredTable.put(key, table.get(key));
            }
        }
        return filteredTable;
    }

    /**
     * This method removes all the records not corresponding to a specific book type
     * @param table
     * @return
     */
    protected LinkedHashMap<String, ArrayList<String>> filterRows(LinkedHashMap<String, ArrayList<String>> table) {
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        String type = getAllAttributes().get("type"); // get the model default type
        ArrayList<String> typeColumn = table.get("type");
        int size = typeColumn.size();

        // iterate through the type column and compare the type with the model default type
        for (int i = 0; i < size; i++) {
            if (!typeColumn.get(i).equals(type)) {
                // iteterate through every column, and remove the cells of that index if not of the same type
                for (String key : filteredTable.keySet()) {
                    ArrayList<String> column = filteredTable.get(key);
                    column.remove(i);
                    filteredTable.put(key, column);
                }
                i--;
                size--;
            }
        }

        return filteredTable;
    }

    public LinkedHashMap<String, String> getAllAttributes() {
        return new LinkedHashMap<>() {{
            put("id", "");
            put("title", "");
            put("location", "");
            put("type", "");
            put("nbr_copies", "");
            put("nbr_borrow", "");
            put("copies_left", "");
        }};
    }

    public String getTableName() {
        return "Document";
    }


}
