package com.rra.vehicletracking.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vehicle_transfer")
public class VehicleTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_owner_id", nullable = false)
    private Owner fromOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_owner_id", nullable = false)
    private Owner toOwner;

    @Column(nullable = false)
    private Double transferPrice;

    @Column(nullable = false)
    private LocalDateTime transferDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_plate_number_id")
    private PlateNumber newPlateNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
