package com.example.bookstore.security.repositories;

import com.example.bookstore.security.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query(
            value = "SELECT * FROM roles WHERE role_id NOT IN (SELECT role_id FROM users_roles WHERE user_id = ?1)",
            nativeQuery = true
    )
    Set<Role> getUserNotAssignedRoles(Integer userId);

}
