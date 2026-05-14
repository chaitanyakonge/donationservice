package com.ashram.donation.service;

import com.ashram.donation.entity.Donor;
import com.ashram.donation.exception.ResourceNotFoundException;
import com.ashram.donation.repository.DonorRepository;
import com.ashram.donation.util.EpochUtil;

import java.util.List;
import java.util.UUID;


public class DonorService {

    private final DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    public String saveOrUpdateDonor(Donor donor) {
        // Check if donor already exists by contact number
        List<Donor> existing = donorRepository.findByContactNumber(donor.getContactNumber());
        if (!existing.isEmpty()) {
            Donor existingDonor = existing.getFirst();
            existingDonor.setFirstName(donor.getFirstName());
            existingDonor.setLastName(donor.getLastName());
            existingDonor.setFullName(buildFullName(donor.getFirstName(), donor.getLastName()));
            existingDonor.setEmail(donor.getEmail());
            existingDonor.setPanNumber(donor.getPanNumber());
            existingDonor.setAddress(donor.getAddress());
            existingDonor.setUpdatedAt(EpochUtil.nowInSeconds());
            donorRepository.save(existingDonor);
            return existingDonor.getDonorId();
        }

        // New donor
        String donorId = UUID.randomUUID().toString();
        long now = EpochUtil.nowInSeconds();
        donor.setDonorId(donorId);
        donor.setPk("DONOR#" + donorId);
        donor.setFullName(buildFullName(donor.getFirstName(), donor.getLastName()));
        donor.setIsDeleted(false);
        donor.setCreatedAt(now);
        donor.setUpdatedAt(now);
        donorRepository.save(donor);
        return donorId;
    }

    public Donor getDonorById(String donorId) {
        return donorRepository.findByPk("DONOR#" + donorId)
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found: " + donorId));
    }

    public List<Donor> searchByContactNumber(String contactNumber) {
        return donorRepository.findByContactNumber(contactNumber)
                .stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .toList();
    }

    public List<Donor> searchByName(String name) {
        return donorRepository.findByFullName(name.trim().toLowerCase())
                .stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .toList();
    }

    public void softDeleteDonor(String donorId, String deletedBy) {
        Donor donor = getDonorById(donorId);
        donor.setIsDeleted(true);
        donor.setDeletedAt(EpochUtil.nowInSeconds());
        donor.setDeletedBy(deletedBy);
        donor.setUpdatedAt(EpochUtil.nowInSeconds());
        donorRepository.save(donor);
    }

    private String buildFullName(String firstName, String lastName) {
        return (firstName.trim() + " " + lastName.trim()).toLowerCase();
    }
}
