package controller;

public interface TableController {
    /**
     * Handles the creation of a new adherant and insertion into the database
     * @return true if executed properly and false otherwise
     */
    public abstract boolean create();

    /**
     * Handles the selection of an adherant from the database
     * @return true if executed properly and false otherwise
     */
    public abstract boolean select();
    public abstract boolean selectAll();
    public abstract boolean update();
    public abstract boolean delete();
}
