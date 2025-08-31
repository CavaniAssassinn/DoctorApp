/* IRepositoryDoctor.java
Doctor model class
Author : Matthew Michael Engelbrecht(222381086)
Date : March 2025
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Doctor;

import java.util.List;
import java.util.Optional;

public interface IRepositoryDoctor extends IRepository<Doctor, String> {
    Optional<Doctor> readString(String id);
    void deleteString(String id);

    List<Doctor> findAll();
}
