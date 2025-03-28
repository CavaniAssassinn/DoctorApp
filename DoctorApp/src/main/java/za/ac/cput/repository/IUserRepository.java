/* IUser Repository. java
Author: Nompumelelo Bhebhe(221584455)
Date: March 2025 */

package za.ac.cput.repository;

import java.util.List;
import java.util.Optional;

    public interface IUserRepository<T> {
        T create(T t);
        Optional<T> read(int id);
        T update(T t);
        boolean delete(int id);
        List<T> findAll();
    }

