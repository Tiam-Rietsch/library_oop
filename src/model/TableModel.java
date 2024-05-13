package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class TableModel {
    // sql utilities
    protected Statement statement;
    protected ResultSet resultSet;

        /**
     * Creates and sqlite connection to the database
     * @return java.sql.Connection
     */
    protected  Connection getConnection() {
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
     * This method executes the sql insert query generated from user attributes
     * @param attributes attributes to be added into the query
     * @return true if executed properly and false otherwise
     * @see createInsertQuery
     */
    public boolean insert(LinkedHashMap<String, String> attributes) {
        // retrieve the query for the insertion with all attributes
        RequestFactory factory = new RequestFactory(getTableName());
        String query = factory.createInsertQuery(attributes);

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
     * this method executes the sql update query generated from user attributes
     * @param id the id of the record to be updated
     * @param attributes the attributes to be updated in the database
     * @return true if executed properly and false otherwise
     */
    public boolean update(String id, LinkedHashMap<String, String> attributes) {
        // retrieve the query for the insertion with all attributes
        RequestFactory factory = new RequestFactory(getTableName());
        String query = factory.createUpdateQuery(id, attributes);

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
     * this method executes the sql delete query generated from user attributes
     * @param id the id of the record to be updated
     * @return true if executed properly and false otherwise
     */
    public boolean delete(String id) {
        RequestFactory factory = new RequestFactory(getTableName());
        String query = factory.createDeleteQuery(id);
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
        RequestFactory factory = new RequestFactory(getTableName());
        String query = factory.createSelectQuery(attributes);
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

    /**
     * This method returns all the attributes of a specific model as in the database
     * @return HashMap of attributes key = attribute name, value = default value
     */
    public abstract LinkedHashMap<String, String> getAllAttributes();

    /**
     * 
     * @return the name of the model database table
     */
    public abstract String getTableName();

}
