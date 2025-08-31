/* User Repository. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025 */

package za.ac.cput.repository;

import za.ac.cput.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static UserRepository repository = null;
    private List<User> userList;

    private UserRepository() {
        this.userList = new ArrayList<>();
    }

    public static UserRepository getInstance() {
        if (repository == null) {
            repository = new UserRepository();
        }
        return repository;
    }

    public User create(User user) {
        this.userList.add(user);
        return user;
    }

    public Optional<User> read(int userId) {
        return this.userList.stream().filter(user -> user.getUserId() == userId).findFirst();
    }

    public User update(User updatedUser) {
        Optional<User> existingUser = read(updatedUser.getUserId());
        if (existingUser.isPresent()) {
            userList.remove(existingUser.get());
            userList.add(updatedUser);
            return updatedUser;
        }
        return null;
    }

    public boolean delete(int userId) {
        Optional<User> userToDelete = read(userId);
        return userToDelete.map(user -> userList.remove(user)).orElse(false);
    }

    public List<User> findAll() {
        return new ArrayList<>(this.userList);
    }
}
