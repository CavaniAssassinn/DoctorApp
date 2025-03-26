package za.ac.cput.factory;

import za.ac.cput.domain.Admin;

public class AdminFactory {
    public static Admin createAdmin(int adminID, String role) {
        return new Admin.Builder()
                .setAdminID(adminID)
                .setRole(role)
                .build();
    }
}
