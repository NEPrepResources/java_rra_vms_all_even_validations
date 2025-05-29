package com.rra.vehicletracking.repository;

import com.rra.vehicletracking.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v WHERE v.chassisNumber LIKE %:query% OR v.plateNumber.plateNumber LIKE %:query% OR v.owner.nationalId LIKE %:query%")
    Page<Vehicle> searchByQuery(String query, Pageable pageable);

    boolean existsByChassisNumber(String chassisNumber);

    boolean existsByOwnerId(Long ownerId);
}
