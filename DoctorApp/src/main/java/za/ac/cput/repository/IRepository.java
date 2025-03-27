package za.ac.cput.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    // Create an entity
    T create(T entity);

    // Read an entity by its ID
    Optional<T> read(int id);
    Optional<T> readString(String id);

    // Update an entity
    T update(T entity);

    // Delete an entity by its ID
    boolean delete(int id);
    boolean deleteString(String id);

    // Find all entities
    List<T> findAll();
}
