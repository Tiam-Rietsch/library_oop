package model;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

public class LoanModel extends TableModel {
    public LinkedHashMap<String, String> getAllAttributes() {
        return new LinkedHashMap<String, String>() {{
            put("id", "");
            put("adherant_id", "");
            put("document_id", "");
            put("lending_date", "");
            put("due_date", "");
            put("status", "");
        }};
    }

    public String getTableName() {
        return "Loan";
    }

    private LocalDate stringToDate(String dateString) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateString, format);
        return date;    
    }

    private int getDaysLeft(LocalDate endDate) {
        LocalDate startDate = LocalDate.now();
        Duration diff = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        return (int) diff.toDays();
    }

    protected LinkedHashMap<String, ArrayList<String>> filterLateRows(LinkedHashMap<String, ArrayList<String>> table) {
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        ArrayList<String> dueDateColumn = table.get("due_date");

        int size = dueDateColumn.size();
        for (int i = 0; i < size; i++) {
            LocalDate dueDate = stringToDate(dueDateColumn.get(i));
            if (getDaysLeft(dueDate) > 0) {
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


    protected LinkedHashMap<String, ArrayList<String>> filterActiveRows(LinkedHashMap<String, ArrayList<String>> table) {
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        ArrayList<String> statusColumn = table.get("status");

        
        int size = statusColumn.size();
        for (int i = 0; i < size; i++) {
            if (!statusColumn.get(i).equals("active")) {
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

    protected LinkedHashMap<String, ArrayList<String>> filterReturnedRows(LinkedHashMap<String, ArrayList<String>> table) {
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        ArrayList<String> statusColumn = table.get("status");

        
        int size = statusColumn.size();
        for (int i = 0; i < size; i++) {
            if (!statusColumn.get(i).equals("returned")) {
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

    public int getActiveLoanCount(String adherantID) {
        String query = "SELECT Loan.id FROM LOAN INNER JOIN Adherant ON Adherant.id=Loan.adherant_id WHERE Adherant.id=" + adherantID + " AND Loan.status='active'";

        System.out.println(query);
        int count = 0;
        try {
            Connection con = getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);

            // get column meta data (usefull for knowing the column names and iterating through them)
            ResultSetMetaData columnData = resultSet.getMetaData();
            
            // iterate through every row and update the LinkedHashMap of results
            while (resultSet.next()) {
                count++;
            }

            con.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public LinkedHashMap<String, ArrayList<String>> select(LinkedHashMap<String, String> attributes) {
        // retrieve the query for selection with all attributes
        RequestFactory factory = new RequestFactory(getTableName());
        String query = factory.createJoinSelect(
            attributes,
            new ArrayList<String>(Arrays.asList("Adherant", "Document")), 
            new ArrayList<String>(Arrays.asList("Loan.id", "Loan.lending_date", "Loan.due_date", "Loan.status",
                            "Adherant.name", "Adherant.type", "Document.title", "Document.type as 'book type'"))
        );
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

    public LinkedHashMap<String, ArrayList<String>> selectLate(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterLateRows(generalTable);
    }

    public LinkedHashMap<String, ArrayList<String>> selectActive(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterActiveRows(generalTable);
    }

    public LinkedHashMap<String, ArrayList<String>> selectReturned(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterReturnedRows(generalTable);
    }
}
