/*
User Repository Test. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025
*/

package za.ac.cput.repository;

import za.ac.cput.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {
    private UserRepository repository;

    @BeforeEach
    public void setUp() {
        repository = UserRepository.getInstance();
        repository.findAll().forEach(user -> repository.delete(user.getUserId()));  // Ensure clean state before each test
    }

    @Test
    public void testCreateUser() {
        User user = new User.Builder()
                .setUserId(1)
                .setUserName("John Doe")
                .setEmail("john.doe@example.com")
                .setPhoneNumber(123456789)
                .build();

        User createdUser = repository.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getUserId(), createdUser.getUserId());
    }

    @Test
    public void testReadUser() {
        User user = new User.Builder()
                .setUserId(2)
                .setUserName("Jane Doe")
                .setEmail("jane.doe@example.com")
                .setPhoneNumber(987654321)
                .build();
        repository.create(user);

        Optional<User> foundUser = repository.read(2);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUserId(), foundUser.get().getUserId());
    }

    @Test
    public void testUpdateUser() {
        User user = new User.Builder()
                .setUserId(3)
                .setUserName("Alice")
                .setEmail("alice@example.com")
                .setPhoneNumber(555123456)
                .build();
        repository.create(user);

        User updatedUser = new User.Builder()
                .setUserId(3)
                .setUserName("Alice Updated")
                .setEmail("alice.updated@example.com")
                .setPhoneNumber(555987654)
                .build();
        repository.update(updatedUser);

        Optional<User> foundUser = repository.read(3);
        assertTrue(foundUser.isPresent());
        assertEquals("Alice Updated", foundUser.get().getUserName());
    }

    @Test
    public void testDeleteUser() {
        User user = new User.Builder()
                .setUserId(4)
                .setUserName("Bob")
                .setEmail("bob@example.com")
                .setPhoneNumber(444567890)
                .build();
        repository.create(user);

        boolean deleted = repository.delete(4);

        assertTrue(deleted);
        assertFalse(repository.read(4).isPresent());
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User.Builder()
                .setUserId(5)
                .setUserName("Charlie")
                .setEmail("charlie@example.com")
                .setPhoneNumber(111222333)
                .build();

        User user2 = new User.Builder()
                .setUserId(6)
                .setUserName("David")
                .setEmail("david@example.com")
                .setPhoneNumber(666777888)
                .build();
        repository.create(user1);
        repository.create(user2);

        List<User> allUsers = repository.findAll();

        assertEquals(2, allUsers.size());
    }
}
