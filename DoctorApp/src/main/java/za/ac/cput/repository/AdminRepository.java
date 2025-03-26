package za.ac.cput.repository;

import za.ac.cput.domain.Admin;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepository {
    private List<Admin> admins = new ArrayList<>();

    // Create/Add
    public void addAdmin(Admin admin) {
        admins.add(admin);
    }

    // Read
    public Optional<Admin> findAdminById(int adminID) {
        return admins.stream()
                .filter(admin -> admin.getAdminID() == adminID)
                .findFirst();
    }

    // Update
    public Admin updateAdmin(Admin updatedAdmin) {
        Optional<Admin> found = findAdminById(updatedAdmin.getAdminID());
        if (found.isPresent()) {
            admins.remove(found.get());
            admins.add(updatedAdmin);
            return updatedAdmin;
        }
        return null;
    }

    // Delete
    public boolean removeAdmin(int adminID) {
        return admins.removeIf(admin -> admin.getAdminID() == adminID);  // int comparison
    }

    // Utility
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(admins);  // Defensive copy
    }

    public int getAdminCount() {
        return admins.size();
    }
}