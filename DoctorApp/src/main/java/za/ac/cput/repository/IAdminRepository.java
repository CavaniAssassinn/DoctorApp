/* IAdminRepository.java
Admin model class
Author : Nothile Cele - 230894356
Date: March 2025
 */
package za.ac.cput.repository;

import za.ac.cput.domain.Admin;
import java.util.Optional;
import java.util.List;

public interface IAdminRepository extends IRepository<Admin, Integer> {
    void addAdmin(Admin admin);
    Optional<Admin> findAdminById(int adminID);
    boolean removeAdmin(int adminID);
    int getAdminCount();
    List<Admin> findAll();  // Ensure this matches the implementation
}
