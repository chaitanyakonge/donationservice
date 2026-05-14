package com.ashram.donation.handler;

import com.ashram.donation.dto.request.ProcessUnifiedDonationRequest;
import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import com.ashram.donation.service.DonationService;
import com.ashram.donation.service.DonorService;
import com.ashram.donation.service.NotificationService;


public class DonationHandler {

    private final DonorService donorService;
    private final DonationService donationService;
    private final NotificationService notificationService;

    public DonationHandler(DonorService donorService,
                                DonationService donationService,
                                NotificationService notificationService) {
        this.donorService = donorService;
        this.donationService = donationService;
        this.notificationService = notificationService;
    }

    // Note: Implement using DynamoDB TransactWriteItems API to ensure atomic saves.
    // Standard Spring @Transactional does NOT work with DynamoDB out-of-the-box.
    public String processUnifiedDonation(ProcessUnifiedDonationRequest request) {

        // 1. Build Donor from flat request and upsert
        Donor donor = Donor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .panNumber(request.getPanNumber())
                .address(request.getAddress())
                .build();
        String resolvedDonorId = donorService.saveOrUpdateDonor(donor);

        // 2. Build Donation from flat request and attach resolved donorId
        Donation donation = Donation.builder()
                .donorId(resolvedDonorId)
                .amount(request.getAmount())
                .paymentMode(request.getPaymentMode().name())
                .bankReferenceNumber(request.getBankReferenceNumber())
                .eventDescription(request.getEventDescription())
                .status("PENDING")
                .build();

        // 3. Save the Donation
        String transactionId = donationService.recordDonation(donation);

        // 4. Update donation with transaction ID
        donation.setTransactionId(transactionId);

        // 5. Async receipt trigger
        notificationService.sendReceipt(donor, donation);

        return transactionId;
    }
}
