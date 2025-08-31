/* AdminRepositoryTest.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.repository;

import org.junit.jupiter.api.*;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminRepositoryTest {
    private static final AdminRepository repository = AdminRepository.getInstance();
    private static final Admin admin = AdminFactory.createAdmin(
            1, "System Admin", "ADM_001",
            "John Doe", "john@email.com", "0821234567"
    );

    @BeforeEach
    void setUp() {
        repository.create(admin);
    }

    @AfterEach
    void cleanUp() {
        repository.delete(admin.getAdminID());
    }

    @Test
    @Order(1)
    void testCreate() {
        Admin newAdmin = AdminFactory.createAdmin(
                2, "Test Admin", "ADM_002",
                "Test User", "test@email.com", "0831234567"
        );
        Admin created = repository.create(newAdmin);
        assertEquals(newAdmin.getAdminID(), created.getAdminID());
    }

    @Test
    @Order(2)
    void testRead() {
        Optional<Admin> found = repository.read(admin.getAdminID());
        assertTrue(found.isPresent());
        assertEquals(admin.getAdminID(), found.get().getAdminID());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Admin updatedAdmin = AdminFactory.createAdmin(
                admin.getAdminID(),
                "Updated Role",
                admin.getUserID(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhoneNumber()
        );

        repository.update(updatedAdmin);

        Optional<Admin> retrieved = repository.read(admin.getAdminID());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Role", retrieved.get().getRole());
    }

    @Test
    @Order(4)
    void testDelete() {
        repository.delete(admin.getAdminID());
        assertFalse(repository.read(admin.getAdminID()).isPresent());
    }

    @Test
    void testFindAll() {
        List<Admin> admins = repository.findAll();
        assertFalse(admins.isEmpty());
    }

    @Test
    void testSingleton() {
        AdminRepository anotherInstance = AdminRepository.getInstance();
        assertSame(repository, anotherInstance);
    }
}