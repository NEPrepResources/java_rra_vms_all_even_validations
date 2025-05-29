package com.rra.vehicletracking.service;

import com.rra.vehicletracking.dto.PlateNumberDTO;
import com.rra.vehicletracking.entity.Owner;
import com.rra.vehicletracking.entity.PlateNumber;
import com.rra.vehicletracking.exception.CustomException;
import com.rra.vehicletracking.repository.OwnerRepository;
import com.rra.vehicletracking.repository.PlateNumberRepository;
import com.rra.vehicletracking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PlateNumberService {

    private static final Logger logger = LoggerFactory.getLogger(PlateNumberService.class);
    private final PlateNumberRepository plateNumberRepository;
    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;

    public PlateNumberService(PlateNumberRepository plateNumberRepository, OwnerRepository ownerRepository, VehicleRepository vehicleRepository) {
        this.plateNumberRepository = plateNumberRepository;
        this.ownerRepository = ownerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public PlateNumberDTO registerPlateNumber(PlateNumberDTO plateNumberDTO) {
        if (plateNumberRepository.existsByPlateNumber(plateNumberDTO.getPlateNumber())) {
            throw new CustomException("Plate number " + plateNumberDTO.getPlateNumber() + " already exists");
        }
        Owner owner = ownerRepository.findById(plateNumberDTO.getOwnerId())
                .orElseThrow(() -> new CustomException("Owner not found"));
        PlateNumber plateNumber = new PlateNumber();
        plateNumber.setPlateNumber(plateNumberDTO.getPlateNumber());
        plateNumber.setOwner(owner);
        plateNumber.setIssuedDate(plateNumberDTO.getIssuedDate());
        plateNumber.setInUse(false);
        plateNumber = plateNumberRepository.save(plateNumber);
        logger.info("Plate number registered: {}", plateNumber.getPlateNumber());

        // Create and return DTO with correct values
        PlateNumberDTO dto = new PlateNumberDTO();
        dto.setId(plateNumber.getId());
        dto.setPlateNumber(plateNumber.getPlateNumber());
        dto.setOwnerId(plateNumber.getOwner().getId());
        dto.setIssuedDate(plateNumber.getIssuedDate());
        dto.setInUse(plateNumber.isInUse());
        return dto;
    }

    public Page<PlateNumberDTO> listAllPlateNumbers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return plateNumberRepository.findAll(pageable)
                .map(plateNumber -> {
                    PlateNumberDTO dto = new PlateNumberDTO();
                    dto.setId(plateNumber.getId());
                    dto.setPlateNumber(plateNumber.getPlateNumber());
                    dto.setOwnerId(plateNumber.getOwner().getId());
                    dto.setIssuedDate(plateNumber.getIssuedDate());
                    dto.setInUse(plateNumber.isInUse());
                    return dto;
                });
    }

    public Page<PlateNumberDTO> listPlateNumbersByOwner(Long ownerId, int page, int size) {
        if (!ownerRepository.existsById(ownerId)) {
            throw new CustomException("Owner not found");
        }
        Pageable pageable = PageRequest.of(page, size);
        return plateNumberRepository.findByOwnerId(ownerId, pageable)
                .map(plateNumber -> {
                    PlateNumberDTO dto = new PlateNumberDTO();
                    dto.setId(plateNumber.getId());
                    dto.setPlateNumber(plateNumber.getPlateNumber());
                    dto.setOwnerId(plateNumber.getOwner().getId());
                    dto.setIssuedDate(plateNumber.getIssuedDate());
                    dto.setInUse(plateNumber.isInUse());
                    return dto;
                });
    }

    public PlateNumberDTO updatePlateNumber(Long id, PlateNumberDTO plateNumberDTO) {
        PlateNumber plateNumber = plateNumberRepository.findById(id)
                .orElseThrow(() -> new CustomException("Plate number not found"));
        if (!plateNumber.getPlateNumber().equals(plateNumberDTO.getPlateNumber()) &&
                plateNumberRepository.existsByPlateNumber(plateNumberDTO.getPlateNumber())) {
            throw new CustomException("Plate number " + plateNumberDTO.getPlateNumber() + " already exists");
        }
        Owner owner = ownerRepository.findById(plateNumberDTO.getOwnerId())
                .orElseThrow(() -> new CustomException("Owner not found"));
        if (plateNumber.isInUse() && !plateNumber.getOwner().getId().equals(owner.getId())) {
            throw new CustomException("Cannot change owner of an in-use plate number");
        }
        plateNumber.setPlateNumber(plateNumberDTO.getPlateNumber());
        plateNumber.setOwner(owner);
        plateNumber.setIssuedDate(plateNumberDTO.getIssuedDate());
        plateNumber = plateNumberRepository.save(plateNumber);
        logger.info("Plate number updated: {}", plateNumber.getPlateNumber());

        // Create and return DTO with correct values
        PlateNumberDTO dto = new PlateNumberDTO();
        dto.setId(plateNumber.getId());
        dto.setPlateNumber(plateNumber.getPlateNumber());
        dto.setOwnerId(plateNumber.getOwner().getId());
        dto.setIssuedDate(plateNumber.getIssuedDate());
        dto.setInUse(plateNumber.isInUse());
        return dto;
    }

    public PlateNumberDTO getPlateNumberById(Long id) {
        PlateNumber plateNumber = plateNumberRepository.findById(id)
                .orElseThrow(() -> new CustomException("Plate number not found"));

        PlateNumberDTO dto = new PlateNumberDTO();
        dto.setId(plateNumber.getId());
        dto.setPlateNumber(plateNumber.getPlateNumber());
        dto.setOwnerId(plateNumber.getOwner().getId());
        dto.setIssuedDate(plateNumber.getIssuedDate());
        dto.setInUse(plateNumber.isInUse());

        return dto;
    }

    public void deletePlateNumber(Long id) {
        PlateNumber plateNumber = plateNumberRepository.findById(id)
                .orElseThrow(() -> new CustomException("Plate number not found"));
        if (plateNumber.isInUse()) {
            throw new CustomException("Cannot delete an in-use plate number");
        }
        plateNumberRepository.delete(plateNumber);
        logger.info("Plate number deleted: {}", plateNumber.getPlateNumber());
    }
}
