/* User Repository. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025 */

package za.ac.cput.factory;

import za.ac.cput.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    public void createUser_ValidInput() {
        int userId = 1;
        String userName = "John Doe";
        String email = "john.doe@example.com";
        int phoneNumber = 123456789;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);

        assertNotNull(user);
        assertEquals(userId, user.getUserId());
        assertEquals(userName, user.getUserName());
        assertEquals(email, user.getEmail());
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    public void createUser_NegativeUserId() {
        int userId = -1;
        String userName = "Jane Doe";
        String email = "jane.doe@example.com";
        int phoneNumber = 987654321;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);
        assertNull(user, "User should be null when userId is negative");
    }

    @Test
    public void createUser_NullUserName() {
        int userId = 2;
        String userName = null;
        String email = "jane.doe@example.com";
        int phoneNumber = 987654321;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);
        assertNull(user, "User should be null when userName is null");
    }

    @Test
    public void createUser_EmptyUserName() {
        int userId = 3;
        String userName = "";
        String email = "jane.doe@example.com";
        int phoneNumber = 987654321;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);
        assertNull(user, "User should be null when userName is empty");
    }

    @Test
    public void createUser_InvalidEmail() {
        int userId = 4;
        String userName = "Alice";
        String email = "invalid-email";
        int phoneNumber = 123456789;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);
        assertNull(user, "User should be null when email is invalid");
    }

    @Test
    public void createUser_NegativePhoneNumber() {
        int userId = 5;
        String userName = "Bob";
        String email = "bob@example.com";
        int phoneNumber = -12345;

        User user = UserFactory.createUser(userId, userName, email, phoneNumber);
        assertNull(user, "User should be null when phoneNumber is negative");
    }
}
