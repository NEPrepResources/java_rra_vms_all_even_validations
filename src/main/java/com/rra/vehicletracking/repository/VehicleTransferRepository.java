package com.rra.vehicletracking.repository;

import com.rra.vehicletracking.entity.VehicleTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VehicleTransferRepository extends JpaRepository<VehicleTransfer, Long> {
    @Query("SELECT vt FROM VehicleTransfer vt WHERE vt.vehicle.chassisNumber = :identifier OR vt.newPlateNumber.plateNumber = :identifier")
    Page<VehicleTransfer> findByVehicleIdentifier(String identifier, Pageable pageable);

    Page<VehicleTransfer> findByVehicleId(Long vehicleId, Pageable pageable);

    @Query("SELECT vt FROM VehicleTransfer vt WHERE vt.vehicle.id = :vehicleId")
    List<VehicleTransfer> findAllByVehicleId(Long vehicleId);
}