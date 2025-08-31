/* AdminRepository.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Admin;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepository implements IAdminRepository {
    private static AdminRepository instance;
    private final List<Admin> admins;

    private AdminRepository() {
        admins = new ArrayList<>();
    }

    public static synchronized AdminRepository getInstance() {
        if (instance == null) {
            instance = new AdminRepository();
        }
        return instance;
    }

    // IRepository<Admin, Integer> methods
    @Override
    public Admin create(Admin admin) {
        admins.add(admin);
        return admin;
    }

    @Override
    public Optional<Admin> read(Integer adminID) {
        return findAdminById(adminID);
    }

    @Override
    public Admin update(Admin updatedAdmin) {
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getAdminID() == updatedAdmin.getAdminID()) {
                admins.set(i, updatedAdmin);
                return updatedAdmin;
            }
        }
        return null;
    }

    @Override
    public void delete(Integer adminID) {
        admins.removeIf(admin -> admin.getAdminID() == adminID);
    }

    // IAdminRepository methods
    @Override
    public void addAdmin(Admin admin) {
        create(admin);
    }

    @Override
    public Optional<Admin> findAdminById(int adminID) {
        return admins.stream()
                .filter(admin -> admin.getAdminID() == adminID)
                .findFirst();
    }

    @Override
    public boolean removeAdmin(int adminID) {
        return admins.removeIf(admin -> admin.getAdminID() == adminID);
    }

    @Override
    public int getAdminCount() {
        return admins.size();
    }

    @Override
    public List<Admin> findAll() {
        return new ArrayList<>(admins);  // Defensive copy
    }
}