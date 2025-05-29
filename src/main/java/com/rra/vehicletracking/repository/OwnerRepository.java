package com.rra.vehicletracking.repository;

import com.rra.vehicletracking.entity.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    @Query("SELECT o FROM Owner o WHERE o.nationalId LIKE %:query% OR o.email LIKE %:query% OR o.phone LIKE %:query%")
    Page<Owner> searchByQuery(String query, Pageable pageable);
    boolean existsByNationalId(String nationalId);
}