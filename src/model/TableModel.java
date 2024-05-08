package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public abstract class TableModel {
    // sql utilities
    private Statement statement;
    private ResultSet resultSet;

    /**
     * Creates and sqlite connection to the database
     * @return java.sql.Connection
     */
    private  Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = null;
            String url = "jdbc:sqlite:src/database/database.db";
            con = DriverManager.getConnection(url);
            return  con;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method assembles an sql select query considering the attributes for the
     * where clause if necessary
     * @param attributes attributes to be added to the where clause
     * @return a String corresponding to the sql command to execute
     */
    private String createSelectQuery(LinkedHashMap<String, String> attributes) {
        String query;

        if (attributes == null || attributes.isEmpty()) {
            // return full selection if no attributes are given
            query = "SELECT * FROM " + getTableName() + ";";
        } else {
            query = "SELECT * FROM " + getTableName() + " WHERE";

            // iterate through all the attributes and add them into the query
            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                String value = attributes.get(key);
                query += " " + key + "=" + value;
                // format the end of the attribute differently if it is the last one or not
                query += key != keys.toArray()[keys.size() -1] ? " AND" : ";";
            }            
        }
        return query;
    }

    /**
     * This method assembles an sql insert query with the various values to insert
     * @param attributes attributes to be added into the table
     * @return a String corresponding to the sql command to execute
     */
    private String createInsertQuery(LinkedHashMap<String, String> attributes) {
        String query = "INSERT INTO " + getTableName() + " (";

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

    /**
     * This method executes the sql insert query generated from user attributes
     * @param attributes attributes to be added into the query
     * @return true if executed properly and false otherwise
     * @see createInsertQuery
     */
    public boolean insert(LinkedHashMap<String, String> attributes) {
        // retrieve the query for the insertion with all attributes
        String query = createInsertQuery(attributes);

        try {
            Connection conn = getConnection();
            statement = conn.createStatement();
            statement.execute(query);

            conn.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This method executes the sql select query generated from user attributes
     * @param attributes attributes to be added into the query
     * @return a LinkedHashMap corresponding to the table returned by the selection
     * @see createSelectQuery
     */
    public LinkedHashMap<String, ArrayList<String>> select(LinkedHashMap<String, String> attributes) {
        // retrieve the query for selection with all attributes
        String query = createSelectQuery(attributes);
        // this is to store the result of the selection query
        LinkedHashMap<String, ArrayList<String>> result = new LinkedHashMap<>();

        try {
            Connection con = getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);

            // get column meta data (usefull for knowing the column names and iterating through them)
            ResultSetMetaData columnData = resultSet.getMetaData();
            
            // iterate through every row and update the LinkedHashMap of results
            while (resultSet.next()) {
                // iterate through every column of the table and add the row values in each column
                for (int i = 1; i <= columnData.getColumnCount(); i++) {
                    String columnName = columnData.getColumnName(i);
                    ArrayList<String> column;

                    // create an empty column in the table if empty or  get current table if existing
                    column = result.get(columnName) == null ? new ArrayList<>() : result.get(columnName);
                    
                    // update the column with the new row value and save it in table Map
                    column.add(resultSet.getString(columnName));
                    result.put(columnName, column);
                }
            }

            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public abstract LinkedHashMap<String, String> getAllAttributes();

    public abstract String getTableName();

}
