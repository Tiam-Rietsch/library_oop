package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class RequestFactory {
    private String tableName;

    public RequestFactory(String tableName) {
        this.tableName = tableName;
    }

    /**
     * This method creates an sql update query with the given attributes
     * @param id id of the record to update
     * @param attributes attributes to be updated
     * @return a String corresponding to the sql command to execute
     */
    public String createUpdateQuery(String id, LinkedHashMap<String, String> attributes) {
        String query = "UPDATE " + this.tableName + " SET";
        Set<String> keys = attributes.keySet();
        for (String key : keys) {
            query += " " + key + "=\'" + attributes.get(key) +"\'";
            query += key == keys.toArray()[keys.size() - 1] ? " WHERE id=" + id + ";" : ",";
        }

        System.err.println(query);
        return query;
    }

    public String createDeleteQuery(String id) {
        return "DELETE FROM " + this.tableName + " WHERE id=\'" + id + "\';";
    }

    /**
     * This method assembles an sql select query considering the attributes for the
     * where clause if necessary
     * @param attributes attributes to be added to the where clause
     * @return a String corresponding to the sql command to execute
     */
    public String createSelectQuery(LinkedHashMap<String, String> attributes) {
        String query;

        if (attributes == null || attributes.isEmpty()) {
            // return full selection if no attributes are given
            query = "SELECT * FROM " + this.tableName + ";";
        } else {
            query = "SELECT * FROM " + this.tableName + " WHERE";

            // iterate through all the attributes and add them into the query
            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                String value = attributes.get(key);
                query += " " + key + "=\'" + value + "\'";
                // format the end of the attribute differently if it is the last one or not
                query += key != keys.toArray()[keys.size() -1] ? " AND" : ";";
            }            
        }

        System.out.println(query);
        return query;
    }

    public String createJoinSelect(LinkedHashMap<String, String> attributes, ArrayList<String> jointTables,
                ArrayList<String> columns) {
        String query = "SELECT ";

        for (String col : columns) {
            query += columns.getLast().equals(col) ? col + " FROM " + this.tableName : col + ",";
        }
        
        for (String table : jointTables) {
            query += " INNER JOIN " + table + " ON " + table + ".id=" + this.tableName + "." + table.toLowerCase() + "_id";
        }

        if (attributes == null || attributes.isEmpty()) {
            // return full selection if no attributes are given
            query += ";";
        } else {
            query += " WHERE";

            // iterate through all the attributes and add them into the query
            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                String value = attributes.get(key);
                query += " " + key + "=\'" + value + "\'";
                // format the end of the attribute differently if it is the last one or not
                query += key != keys.toArray()[keys.size() -1] ? " AND" : ";";
            }            
        }

        System.out.println(query);
        return query;
    }

    /**
     * This method assembles an sql insert query with the various values to insert
     * @param attributes attributes to be added into the table
     * @return a String corresponding to the sql command to execute
     */
    public String createInsertQuery(LinkedHashMap<String, String> attributes) {
        String query = "INSERT INTO " + this.tableName + " (";

        // iterate through all the attributes and add them to the query (column names part)
        Set<String> keys = attributes.keySet();
        for (String key: keys) {
            if (key.equals("id")) continue;
            query += key;
            // format the end of the attribute differently if it is the last one or not
            query += key != keys.toArray()[keys.size() -1] ? "," : ") VALUES (";
        }
        // iterate through all the attributes and add them to the query (values part)
        for (String key: keys) {
            if (key.equals("id")) continue;
            query += "\'" + attributes.get(key) + "\'";
            // format the end of the attribute differently if it is the last one or not
            query += key != keys.toArray()[keys.size() -1] ? "," : ");";
        }
        return query;
    }

}
