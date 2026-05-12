package com.ashram.donation.service;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final SesClient sesClient;
    private final DonationService donationService;
    private final DonorService donorService;

    @Value("${aws.ses.sender-email}")
    private String senderEmail;

    public NotificationService(SesClient sesClient,
                               DonationService donationService,
                               DonorService donorService) {
        this.sesClient = sesClient;
        this.donationService = donationService;
        this.donorService = donorService;
    }

    public void sendReceipt(String transactionId) {
        try {
            logger.info("Sending receipt for transactionId: {}", transactionId);
            Donation donation = donationService.getDonationById(transactionId);
            Donor donor = donorService.getDonorById(donation.getDonorId());

            if (donor.getEmail() == null || donor.getEmail().isBlank()) {
                logger.info("No email found for donor, skipping receipt for transactionId: {}", transactionId);
                return;
            }

            String subject = "Donation Receipt - " + transactionId;
            String body = buildReceiptBody(donor, donation);

            sesClient.sendEmail(SendEmailRequest.builder()
                    .source(senderEmail)
                    .destination(Destination.builder().toAddresses(donor.getEmail()).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).build())
                            .body(Body.builder()
                                    .text(Content.builder().data(body).build())
                                    .build())
                            .build())
                    .build());

            donation.setReceiptGenerated(true);
            donationService.recordDonation(donation);
            logger.info("Receipt sent successfully for transactionId: {}, to email: {}", transactionId, donor.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send receipt for transactionId: {}", transactionId, e);
        }
    }

    private String buildReceiptBody(Donor donor, Donation donation) {
        return """
                Dear %s %s,
                
                Thank you for your generous donation.
                
                Transaction ID : %s
                Amount         : ₹%.2f
                Payment Mode   : %s
                Event          : %s
                Status         : %s
                
                This receipt is valid for 80G tax exemption purposes.
                
                With gratitude,
                Ashram Donation Management
                """.formatted(
                donor.getFirstName(),
                donor.getLastName(),
                donation.getTransactionId(),
                donation.getAmount(),
                donation.getPaymentMode(),
                donation.getEventDescription() != null ? donation.getEventDescription() : "General Donation",
                donation.getStatus()
        );
    }
}
