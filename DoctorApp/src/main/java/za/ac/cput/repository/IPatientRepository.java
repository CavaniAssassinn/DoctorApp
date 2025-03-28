/* IPatientRepository.java
Patient model
Author : Bruneez Apollis(222127600)
Date: March 2025
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Patient;

import java.util.List;

public interface IPatientRepository extends IRepository<Patient, Integer> {

    List<Patient> getAll();



    boolean existsById(Integer id);
}
