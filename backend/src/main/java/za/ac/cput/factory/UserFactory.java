/* UserFactory. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025 */
package za.ac.cput.factory;

import za.ac.cput.domain.User;
import za.ac.cput.util.Helper;

public class UserFactory {
    public static User createUser(int userId, String userName, String email, int phoneNumber) {
        if (userId <= 0 || // Ensure userId is positive
                Helper.isNullOrEmpty(userName) ||
                !Helper.isValidEmail(email) ||
                !Helper.isPositiveNumber(phoneNumber)) {
            return null; // Return null if validation fails
        }

        return new User.Builder()
                .setUserId(userId) // Now accepts int
                .setUserName(userName)
                .setEmail(email)
                .setPhoneNumber(phoneNumber)
                .build();
    }
}
