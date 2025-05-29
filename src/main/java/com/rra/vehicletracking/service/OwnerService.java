package com.rra.vehicletracking.service;

import com.rra.vehicletracking.dto.OwnerDTO;
import com.rra.vehicletracking.entity.Owner;
import com.rra.vehicletracking.exception.CustomException;
import com.rra.vehicletracking.repository.OwnerRepository;
import com.rra.vehicletracking.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);
    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;

    public OwnerService(OwnerRepository ownerRepository, VehicleRepository vehicleRepository) {
        this.ownerRepository = ownerRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Owner registerOwner(OwnerDTO ownerDTO) {
        if (ownerRepository.existsByNationalId(ownerDTO.getNationalId())) {
            throw new CustomException("Owner with National ID " + ownerDTO.getNationalId() + " already exists");
        }
        Owner owner = new Owner();
        owner.setName(ownerDTO.getName());
        owner.setNationalId(ownerDTO.getNationalId());
        owner.setPhone(ownerDTO.getPhone());
        owner.setAddress(ownerDTO.getAddress());
        owner.setEmail(ownerDTO.getEmail());
        owner = ownerRepository.save(owner);
        logger.info("Owner registered: {}", owner.getNationalId());
        return owner;
    }
    public Page<OwnerDTO> listOwners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ownerRepository.findAll(pageable)
                .map(owner -> {
                    OwnerDTO dto = new OwnerDTO();
                    dto.setName(owner.getName());
                    dto.setNationalId(owner.getNationalId());
                    dto.setEmail(owner.getEmail());
                    dto.setPhone(owner.getPhone());
                    dto.setAddress(owner.getAddress());
                    return dto;
                });
    }

    public Page<OwnerDTO> searchOwner(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ownerRepository.searchByQuery(query, pageable)
                .map(owner -> {
                    OwnerDTO dto = new OwnerDTO();
                    dto.setName(owner.getName());
                    dto.setNationalId(owner.getNationalId());
                    dto.setEmail(owner.getEmail());
                    dto.setPhone(owner.getPhone());
                    dto.setAddress(owner.getAddress());
                    return dto;
                });
    }

    public Owner updateOwner(Long id, OwnerDTO ownerDTO) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Owner not found"));
        if (!owner.getNationalId().equals(ownerDTO.getNationalId()) &&
                ownerRepository.existsByNationalId(ownerDTO.getNationalId())) {
            throw new CustomException("National ID " + ownerDTO.getNationalId() + " is already in use");
        }
        owner.setName(ownerDTO.getName());
        owner.setNationalId(ownerDTO.getNationalId());
        owner.setPhone(ownerDTO.getPhone());
        owner.setAddress(ownerDTO.getAddress());
        owner.setEmail(ownerDTO.getEmail());
        owner = ownerRepository.save(owner);
        logger.info("Owner updated: {}", owner.getNationalId());
        return owner;
    }
    public Owner deleteOwner(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Owner not found"));
        if (vehicleRepository.existsByOwnerId(id)) {
            throw new CustomException("Cannot delete owner with associated vehicles");
        }

        // Create a copy of the owner details before deletion
        Owner deletedOwner = new Owner();
        deletedOwner.setId(owner.getId());
        deletedOwner.setName(owner.getName());
        deletedOwner.setNationalId(owner.getNationalId());
        deletedOwner.setPhone(owner.getPhone());
        deletedOwner.setAddress(owner.getAddress());
        deletedOwner.setEmail(owner.getEmail());
        deletedOwner.setCreatedAt(owner.getCreatedAt());
        deletedOwner.setUpdatedAt(owner.getUpdatedAt());

        ownerRepository.delete(owner);
        logger.info("Owner deleted: {}", deletedOwner.getNationalId());

        return deletedOwner;
    }
}
