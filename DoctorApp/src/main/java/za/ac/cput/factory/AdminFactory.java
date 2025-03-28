/* AdminFactory.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Admin;
import za.ac.cput.util.Helper;
import java.util.UUID;  // Required import for UUID

public class AdminFactory {

    public static Admin createAdmin(int adminID, String role, String userID,
                                    String name, String email, String phoneNumber) {

        // Validate inputs using Helper class
        Helper.validatePositiveNumber(adminID, "Admin ID must be positive");
        Helper.validateNotNullOrEmpty(role, "Role cannot be null or empty");
        Helper.validateNotNullOrEmpty(userID, "User ID cannot be null or empty");
        Helper.validateNotNullOrEmpty(name, "Name cannot be null or empty");
        Helper.validateEmail(email, "Invalid email format");
        Helper.validateNotNullOrEmpty(phoneNumber, "Phone number cannot be null or empty");

        // Phone number format validation (10 digits)
        if (!phoneNumber.matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }

        return new Admin.Builder()
                .setAdminID(adminID)
                .setRole(role)
                .setUserID(userID)
                .setName(name)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .build();
    }


    public static Admin createBasicAdmin(int adminID, String role) {
        Helper.validatePositiveNumber(adminID, "Admin ID must be positive");
        Helper.validateNotNullOrEmpty(role, "Role cannot be null or empty");

        return new Admin.Builder()
                .setAdminID(adminID)
                .setRole(role)
                .setUserID("ADM_" + Math.abs(UUID.randomUUID().hashCode()))  // Inline UUID generation
                .setName("DEFAULT_ADMIN")
                .setEmail("admin@default.com")
                .setPhoneNumber("0000000000")  // Default valid phone number
                .build();
    }
}
