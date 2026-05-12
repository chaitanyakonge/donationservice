package com.ashram.donation.service;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.exception.ResourceNotFoundException;
import com.ashram.donation.repository.DonationRepository;
import com.ashram.donation.util.EpochUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public String recordDonation(Donation donation) {
        String transactionId = UUID.randomUUID().toString();
        long now = EpochUtil.nowInSeconds();
        donation.setTransactionId(transactionId);
        donation.setPk("DONATION#" + transactionId);
        donation.setTransactionEpoch(now);
        donation.setMonthPartition(EpochUtil.deriveMonthPartition(now));
        donation.setReceiptGenerated(false);
        donation.setIsDeleted(false);
        donation.setCreatedAt(now);
        donation.setUpdatedAt(now);
        donationRepository.save(donation);
        return transactionId;
    }

    public Donation getDonationById(String transactionId) {
        return donationRepository.findByPk("DONATION#" + transactionId)
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found: " + transactionId));
    }

    public void updateTransactionStatus(String transactionId, String status, String utr) {
        Donation donation = getDonationById(transactionId);
        donation.setStatus(status.toUpperCase());
        donation.setBankReferenceNumber(utr);
        donation.setUpdatedAt(EpochUtil.nowInSeconds());
        donationRepository.save(donation);
    }

    public List<Donation> getDonationHistoryForDonor(String donorId) {
        return donationRepository.findByDonorId(donorId)
                .stream()
                .filter(d -> !Boolean.TRUE.equals(d.getIsDeleted()))
                .toList();
    }

    public List<Donation> getDonationsInRange(Long startEpoch, Long endEpoch) {
        List<String> partitions = EpochUtil.getMonthPartitions(startEpoch, endEpoch);
        List<Donation> results = new ArrayList<>();
        for (String partition : partitions) {
            results.addAll(donationRepository.findByMonthPartitionAndEpochRange(partition, startEpoch, endEpoch));
        }
        return results.stream().filter(d -> !Boolean.TRUE.equals(d.getIsDeleted())).toList();
    }

    public List<Donation> getDonationsInRangeByPaymentMode(Long startEpoch, Long endEpoch, String paymentMode) {
        return getDonationsInRange(startEpoch, endEpoch)
                .stream()
                .filter(d -> d.getPaymentMode().equalsIgnoreCase(paymentMode))
                .toList();
    }

    public Double calculateTotalDonationsInRange(Long startEpoch, Long endEpoch) {
        return getDonationsInRange(startEpoch, endEpoch)
                .stream()
                .filter(d -> "SUCCESS".equals(d.getStatus()))
                .mapToDouble(Donation::getAmount)
                .sum();
    }

    public void softDeleteDonation(String transactionId, String deletedBy) {
        Donation donation = getDonationById(transactionId);
        donation.setIsDeleted(true);
        donation.setDeletedAt(EpochUtil.nowInSeconds());
        donation.setDeletedBy(deletedBy);
        donation.setUpdatedAt(EpochUtil.nowInSeconds());
        donationRepository.save(donation);
    }
}
