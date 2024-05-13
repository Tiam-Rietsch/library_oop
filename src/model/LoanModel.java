package model;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import view.View;

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

    /**
     * Converts a local date to string in a given format
     * @param dateString
     * @return
     */
    private LocalDate stringToDate(String dateString) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateString, format);
        return date;    
    }

    /**
     * returns the number of days left after a given end date
     * @param endDate corresponding to the deadline for the loan return
     * @return an integer representing the number of days left (-ve is the endDate has passed)
     */
    private int getDaysLeft(LocalDate endDate) {
        LocalDate startDate = LocalDate.now();
        Duration diff = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
        return (int) diff.toDays();
    }

    private LinkedHashMap<String, ArrayList<String>> filterLateRows(LinkedHashMap<String, ArrayList<String>> table) {
        // copy the current table
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        // get all the cells in the due date column
        ArrayList<String> dueDateColumn = table.get("due_date");

        // iterate through the due_date column and only keep records of loans that passed the due date
        int size = dueDateColumn.size();
        for (int i = 0; i < size; i++) {
            LocalDate dueDate = stringToDate(dueDateColumn.get(i));
            if (getDaysLeft(dueDate) > 0) {
                // to remove a record we remove the correcponding row index in every column
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


    private LinkedHashMap<String, ArrayList<String>> filterActiveRows(LinkedHashMap<String, ArrayList<String>> table) {
        // first get the current table
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        // get the status column
        ArrayList<String> statusColumn = table.get("status");

        // for every element inside the status column, check if the status is active. If not returned, remove it from table
        int size = statusColumn.size();
        for (int i = 0; i < size; i++) {
            if (!statusColumn.get(i).equals("active")) {
                // to remove a record we remove the correcponding row index in every column
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

    /**
     * This method filteres the HashMap and keeps only records of books that were returned
     * @param table
     * @return HashMap containing filtered records
     */
    private LinkedHashMap<String, ArrayList<String>> filterReturnedRows(LinkedHashMap<String, ArrayList<String>> table) {
        // first get the current table
        LinkedHashMap<String, ArrayList<String>> filteredTable = table;
        // get the status column
        ArrayList<String> statusColumn = table.get("status");

        // for every element inside the status column, check if the status is returned. If not returned, remove it from table
        int size = statusColumn.size();
        for (int i = 0; i < size; i++) {
            if (!statusColumn.get(i).equals("returned")) {
                // to remove a record we remove the correcponding row index in every column
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

        int count = 0;
        try {
            Connection con = getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);

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

    public boolean insert(LinkedHashMap<String, String> attrtibutes) {
        String adherantID = attrtibutes.get("adherant_id");
        String documentID = attrtibutes.get("document_id");
        try {
            Connection conn = getConnection();
            statement = conn.createStatement();

            // first we get all the active loans from the table to find if the adherant has borrowed the given book
            resultSet = statement.executeQuery("SELECT * FROM Loan WHERE status='active';");
            while (resultSet.next()) {
                if (resultSet.getString("adherant_id").equals(adherantID) &&
                    resultSet.getString("document_id").equals(documentID)) {
                        View.displayError("Adherant Already borrowed this book");
                        return true;        
                }
            }

            // we get document's number of copies left and number of borrows and increase the number of borrows
            resultSet = statement.executeQuery("SELECT * FROM Document WHERE id="+documentID+";");
            int n_copies = 0;
            int n_borrow = 0;
            int copies_left;
            while (resultSet.next()) {
                n_copies = Integer.parseInt(resultSet.getString("nbr_copies"));
                n_borrow = Integer.parseInt(resultSet.getString("nbr_borrow"));
            }
            n_borrow+= 1;
            copies_left = n_copies - n_borrow;
            // if the number of copies left is negative then there are no copies of the book left
            if (copies_left < 0) {
                View.displayError("There are no more copies of this document");
                return true;
            }

            // we update the document information (number of copies etc...)
            statement.execute("UPDATE DOCUMENT SET copies_left="+copies_left+", nbr_borrow="+n_borrow+ " WHERE id="+documentID+";");

            conn.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return super.insert(attrtibutes);
    }

    public boolean returnDocument(String adherantID, String documentID) {
        // query to update the loan table when the document is returned
        String query = "UPDATE DOCUMENT SET status='returned' WHERE id="+adherantID+" AND document_id="+documentID+" AND status='active';";

        try {
            Connection conn = getConnection();
            statement = conn.createStatement();
            // first we execute the main query to update the loan table
            statement.execute(query);

            // we first get the number of copies and borrows for the document and decrease the number of borrows
            resultSet = statement.executeQuery("SELECT * FROM Document WHERE id="+documentID+";");
            int n_copies = 0;
            int n_borrow = 0;
            int copies_left;
            while (resultSet.next()) {
                n_copies = Integer.parseInt(resultSet.getString("nbr_copies"));
                n_borrow = Integer.parseInt(resultSet.getString("nbr_borrow"));
            }
            n_borrow -= 1;
            copies_left = n_copies - n_borrow;

            // then we update the information into the database
            statement.execute("UPDATE DOCUMENT SET copies_left="+copies_left+", nbr_borrow="+n_borrow+ " WHERE id="+documentID+";");


            conn.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Filters the selection to return only the late adherants
     * @param attributes
     * @return HashMap representing a table of late adherants
     */
    public LinkedHashMap<String, ArrayList<String>> selectLate(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterLateRows(generalTable);
    }

    /**
     * Filteres the selection to return only the active loans (books that were not returned)
     * @param attributes
     * @return HashMap representing a table of active loans
     */
    public LinkedHashMap<String, ArrayList<String>> selectActive(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterActiveRows(generalTable);
    }

    /**
     * Filters the selectionto return the Returned loans
     * @param attributes
     * @return HashMap representing a table of returned loans
     */
    public LinkedHashMap<String, ArrayList<String>> selectReturned(LinkedHashMap<String, String> attributes) {
        LinkedHashMap<String, ArrayList<String>> generalTable = select(attributes);
        return filterReturnedRows(generalTable);
    }
}
