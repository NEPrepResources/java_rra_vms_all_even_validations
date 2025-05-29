package com.rra.vehicletracking.repository;

import com.rra.vehicletracking.entity.PlateNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlateNumberRepository extends JpaRepository<PlateNumber, Long> {
    Page<PlateNumber> findByOwnerId(Long ownerId, Pageable pageable);
    boolean existsByPlateNumber(String plateNumber);
}