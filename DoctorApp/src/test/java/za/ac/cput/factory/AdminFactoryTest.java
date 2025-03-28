/* AdminFactoryTest.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Admin;
import za.ac.cput.repository.AdminRepository;

import static org.junit.jupiter.api.Assertions.*;

class AdminFactoryTest {

    @Test
    void testCreateAdmin() {
        Admin admin = AdminFactory.createAdmin(1, "System Admin", "ADM_001",
                "John Doe", "john@email.com", "0821234567");

        assertNotNull(admin);
        assertEquals(1, admin.getAdminID());
        assertEquals("System Admin", admin.getRole());
        assertEquals("ADM_001", admin.getUserID());
        assertEquals("John Doe", admin.getName());
        assertEquals("john@email.com", admin.getEmail());
        assertEquals("0821234567", admin.getPhoneNumber());
    }

    @Test
    void testCreateBasicAdmin() {
        Admin admin = AdminFactory.createBasicAdmin(2, "Content Moderator");

        assertNotNull(admin);
        assertEquals(2, admin.getAdminID());
        assertEquals("Content Moderator", admin.getRole());
        assertTrue(admin.getUserID().startsWith("ADM_"));
        assertEquals("DEFAULT_ADMIN", admin.getName());
        assertEquals("admin@default.com", admin.getEmail());
        assertEquals("0000000000", admin.getPhoneNumber());
    }

    @Test
    void testSingletonRepository() {
        AdminRepository repo1 = AdminRepository.getInstance();
        AdminRepository repo2 = AdminRepository.getInstance();

        // Test singleton
        assertSame(repo1, repo2, "Both instances should be the same");

        // Test repository functionality
        Admin admin = AdminFactory.createBasicAdmin(3, "Test Admin");
        repo1.create(admin);

        assertEquals(1, repo1.getAdminCount());
        assertEquals(repo1.getAdminCount(), repo2.getAdminCount());
    }

    @Test
    void testInvalidInputs() {
        // Test invalid admin ID
        assertThrows(IllegalArgumentException.class, () ->
                AdminFactory.createAdmin(0, "Admin", "ADM_001", "Name", "email@test.com", "0821234567"));

        // Test null role
        assertThrows(IllegalArgumentException.class, () ->
                AdminFactory.createAdmin(1, null, "ADM_001", "Name", "email@test.com", "0821234567"));

        // Test invalid email
        assertThrows(IllegalArgumentException.class, () ->
                AdminFactory.createAdmin(1, "Admin", "ADM_001", "Name", "invalid-email", "0821234567"));

        // Test invalid phone number
        assertThrows(IllegalArgumentException.class, () ->
                AdminFactory.createAdmin(1, "Admin", "ADM_001", "Name", "email@test.com", "123"));
    }
}