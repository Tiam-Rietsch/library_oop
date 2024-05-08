package controller;

public class AdherantController implements TableController {
    public boolean create() {
        System.out.println("You want to create a new adherant!");
        return true;
    }

    public boolean select() {
        System.out.println("You want to select and adherant!");
        return true;
    }

    public boolean selectAll() {
        System.out.println("You want to select all adherants");
        return true;
    }

    public boolean update() {
        System.out.println("You want to update an adherant!");
        return true;
    }

    public boolean delete() {
        System.out.println("You want to delete an adherant!");
        return true;
    }
}
