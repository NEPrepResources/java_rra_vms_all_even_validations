package com.rra.vehicletracking.repository;

import com.rra.vehicletracking.entity.User;
import com.rra.vehicletracking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNationalId(String nationalId);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);
    Optional<User> findByRole(Role role);
}
