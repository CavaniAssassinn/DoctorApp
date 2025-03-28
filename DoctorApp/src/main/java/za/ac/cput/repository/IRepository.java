package za.ac.cput.repository;

import java.util.Optional;

public interface IRepository<T,ID> {
    T create(T entity);

    Optional<T> read(ID id);

    T update(T entity);

    void delete(ID id);

}
