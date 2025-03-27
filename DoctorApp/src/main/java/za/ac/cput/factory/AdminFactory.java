/* AdminFactory.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.factory;

import za.ac.cput.domain.Admin;

public class AdminFactory {
    public static Admin createAdmin(int adminID, String role) {
        return new Admin.Builder()
                .setAdminID(adminID)
                .setRole(role)
                .setUserID("DEFAULT_USER_ID")
                .setName("DEFAULT_NAME")
                .setEmail("default@email.com")
                .setPhoneNumber("0000000000")
                .build();
    }
}
