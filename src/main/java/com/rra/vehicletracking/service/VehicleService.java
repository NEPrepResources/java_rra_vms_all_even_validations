package com.rra.vehicletracking.service;

import com.rra.vehicletracking.dto.VehicleDTO;
import com.rra.vehicletracking.dto.VehicleTransferDTO;
import com.rra.vehicletracking.entity.Owner;
import com.rra.vehicletracking.entity.PlateNumber;
import com.rra.vehicletracking.entity.Vehicle;
import com.rra.vehicletracking.entity.VehicleTransfer;
import com.rra.vehicletracking.exception.CustomException;
import com.rra.vehicletracking.repository.OwnerRepository;
import com.rra.vehicletracking.repository.PlateNumberRepository;
import com.rra.vehicletracking.repository.VehicleRepository;
import com.rra.vehicletracking.repository.VehicleTransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);
    private final VehicleRepository vehicleRepository;
    private final OwnerRepository ownerRepository;
    private final PlateNumberRepository plateNumberRepository;
    private final VehicleTransferRepository vehicleTransferRepository;

    public VehicleService(VehicleRepository vehicleRepository, OwnerRepository ownerRepository,
                          PlateNumberRepository plateNumberRepository, VehicleTransferRepository vehicleTransferRepository) {
        this.vehicleRepository = vehicleRepository;
        this.ownerRepository = ownerRepository;
        this.plateNumberRepository = plateNumberRepository;
        this.vehicleTransferRepository = vehicleTransferRepository;
    }

    public void registerVehicle(VehicleDTO vehicleDTO) {
        if (vehicleRepository.existsByChassisNumber(vehicleDTO.getChassisNumber())) {
            throw new CustomException("Vehicle with chassis number " + vehicleDTO.getChassisNumber() + " already exists");
        }
        Owner owner = ownerRepository.findById(vehicleDTO.getOwnerId())
                .orElseThrow(() -> new CustomException("Owner not found"));
        PlateNumber plateNumber = plateNumberRepository.findById(vehicleDTO.getPlateNumberId())
                .orElseThrow(() -> new CustomException("Plate number not found"));
        if (plateNumber.isInUse()) {
            throw new CustomException("Plate number is already in use");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        vehicle.setManufactureCompany(vehicleDTO.getManufactureCompany());
        vehicle.setManufactureYear(vehicleDTO.getManufactureYear());
        vehicle.setPrice(vehicleDTO.getPrice());
        vehicle.setModelName(vehicleDTO.getModelName());
        vehicle.setOwner(owner);
        vehicle.setPlateNumber(plateNumber);
        plateNumber.setInUse(true);
        vehicleRepository.save(vehicle);
        plateNumberRepository.save(plateNumber);
        logger.info("Vehicle registered: {}", vehicle.getChassisNumber());
    }

    public Page<VehicleDTO> listVehicles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vehicleRepository.findAll(pageable)
                .map(vehicle -> {
                    VehicleDTO dto = new VehicleDTO();
                    dto.setId(vehicle.getId());
                    dto.setChassisNumber(vehicle.getChassisNumber());
                    dto.setManufactureCompany(vehicle.getManufactureCompany());
                    dto.setManufactureYear(vehicle.getManufactureYear());
                    dto.setPrice(vehicle.getPrice());
                    dto.setModelName(vehicle.getModelName());
                    dto.setOwnerId(vehicle.getOwner().getId());
                    dto.setPlateNumberId(vehicle.getPlateNumber().getId());
                    return dto;
                });
    }

    public Page<VehicleDTO> searchVehicle(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vehicleRepository.searchByQuery(query, pageable)
                .map(vehicle -> {
                    VehicleDTO dto = new VehicleDTO();
                    dto.setId(vehicle.getId());
                    dto.setChassisNumber(vehicle.getChassisNumber());
                    dto.setManufactureCompany(vehicle.getManufactureCompany());
                    dto.setManufactureYear(vehicle.getManufactureYear());
                    dto.setPrice(vehicle.getPrice());
                    dto.setModelName(vehicle.getModelName());
                    dto.setOwnerId(vehicle.getOwner().getId());
                    dto.setPlateNumberId(vehicle.getPlateNumber().getId());
                    return dto;
                });
    }

    public void transferVehicle(VehicleTransferDTO transferDTO) {
        Vehicle vehicle = vehicleRepository.findById(transferDTO.getVehicleId())
                .orElseThrow(() -> new CustomException("Vehicle not found"));
        Owner fromOwner = ownerRepository.findById(transferDTO.getFromOwnerId())
                .orElseThrow(() -> new CustomException("From owner not found"));
        Owner toOwner = ownerRepository.findById(transferDTO.getToOwnerId())
                .orElseThrow(() -> new CustomException("To owner not found"));
        PlateNumber newPlateNumber = plateNumberRepository.findById(transferDTO.getNewPlateNumberId())
                .orElseThrow(() -> new CustomException("New plate number not found"));
        if (newPlateNumber.isInUse()) {
            throw new CustomException("New plate number is already in use");
        }
        if (!vehicle.getOwner().getId().equals(fromOwner.getId())) {
            throw new CustomException("Vehicle does not belong to the specified owner");
        }
        PlateNumber oldPlateNumber = vehicle.getPlateNumber();
        oldPlateNumber.setInUse(false);
        newPlateNumber.setInUse(true);
        vehicle.setOwner(toOwner);
        vehicle.setPlateNumber(newPlateNumber);
        VehicleTransfer transfer = new VehicleTransfer();
        transfer.setVehicle(vehicle);
        transfer.setFromOwner(fromOwner);
        transfer.setToOwner(toOwner);
        transfer.setTransferPrice(transferDTO.getTransferPrice());
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setNewPlateNumber(newPlateNumber);
        vehicleRepository.save(vehicle);
        plateNumberRepository.save(oldPlateNumber);
        plateNumberRepository.save(newPlateNumber);
        vehicleTransferRepository.save(transfer);
        logger.info("Vehicle transferred: {} to owner {}", vehicle.getChassisNumber(), toOwner.getNationalId());
    }

    public Page<VehicleTransferDTO> getOwnershipHistory(String identifier, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vehicleTransferRepository.findByVehicleIdentifier(identifier, pageable)
                .map(transfer -> {
                    VehicleTransferDTO dto = new VehicleTransferDTO();
                    dto.setVehicleId(transfer.getVehicle().getId());
                    dto.setFromOwnerId(transfer.getFromOwner().getId());
                    dto.setToOwnerId(transfer.getToOwner().getId());
                    dto.setTransferPrice(transfer.getTransferPrice());
                    dto.setNewPlateNumberId(transfer.getNewPlateNumber().getId());
                    return dto;
                });
    }

    public Page<VehicleTransferDTO> getOwnershipHistoryByVehicleId(Long vehicleId, int page, int size) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new CustomException("Vehicle not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        return vehicleTransferRepository.findByVehicleId(vehicleId, pageable)
                .map(transfer -> {
                    VehicleTransferDTO dto = new VehicleTransferDTO();
                    dto.setVehicleId(transfer.getVehicle().getId());
                    dto.setFromOwnerId(transfer.getFromOwner().getId());
                    dto.setToOwnerId(transfer.getToOwner().getId());
                    dto.setTransferPrice(transfer.getTransferPrice());
                    dto.setNewPlateNumberId(transfer.getNewPlateNumber().getId());
                    return dto;
                });
    }

    public void updateVehicle(Long id, VehicleDTO vehicleDTO) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found"));
        if (!vehicle.getChassisNumber().equals(vehicleDTO.getChassisNumber()) &&
                vehicleRepository.existsByChassisNumber(vehicleDTO.getChassisNumber())) {
            throw new CustomException("Chassis number " + vehicleDTO.getChassisNumber() + " already exists");
        }
        Owner owner = ownerRepository.findById(vehicleDTO.getOwnerId())
                .orElseThrow(() -> new CustomException("Owner not found"));
        PlateNumber plateNumber = plateNumberRepository.findById(vehicleDTO.getPlateNumberId())
                .orElseThrow(() -> new CustomException("Plate number not found"));
        if (!vehicle.getPlateNumber().getId().equals(plateNumber.getId()) && plateNumber.isInUse()) {
            throw new CustomException("New plate number is already in use");
        }
        vehicle.getPlateNumber().setInUse(false);
        plateNumber.setInUse(true);
        vehicle.setChassisNumber(vehicleDTO.getChassisNumber());
        vehicle.setManufactureCompany(vehicleDTO.getManufactureCompany());
        vehicle.setManufactureYear(vehicleDTO.getManufactureYear());
        vehicle.setPrice(vehicleDTO.getPrice());
        vehicle.setModelName(vehicleDTO.getModelName());
        vehicle.setOwner(owner);
        vehicle.setPlateNumber(plateNumber);
        vehicleRepository.save(vehicle);
        plateNumberRepository.save(plateNumber);
        plateNumberRepository.save(vehicle.getPlateNumber());
        logger.info("Vehicle updated: {}", vehicle.getChassisNumber());
    }

    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found"));

        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setChassisNumber(vehicle.getChassisNumber());
        dto.setManufactureCompany(vehicle.getManufactureCompany());
        dto.setManufactureYear(vehicle.getManufactureYear());
        dto.setPrice(vehicle.getPrice());
        dto.setModelName(vehicle.getModelName());
        dto.setOwnerId(vehicle.getOwner().getId());
        dto.setPlateNumberId(vehicle.getPlateNumber().getId());

        return dto;
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Vehicle not found"));
        PlateNumber plateNumber = vehicle.getPlateNumber();
        plateNumber.setInUse(false);
        vehicleRepository.delete(vehicle);
        plateNumberRepository.save(plateNumber);
        logger.info("Vehicle deleted: {}", vehicle.getChassisNumber());
    }
}
