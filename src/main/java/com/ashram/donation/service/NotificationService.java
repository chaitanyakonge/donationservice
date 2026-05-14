package com.ashram.donation.service;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;


public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final SesClient sesClient;
    private final String senderEmail;

    public NotificationService() {
        this.sesClient = SesClient.builder()
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION_OVERRIDE", "ap-south-1")))
                .build();
        this.senderEmail = System.getenv("AWS_SES_SENDER_EMAIL");
    }

    public void sendReceipt(Donor donor, Donation donation) {
        try {
            logger.info("Sending receipt for transactionId: {}", donation.getTransactionId());

            if (donor.getEmail() == null || donor.getEmail().isBlank()) {
                logger.info("No email found for donor, skipping receipt for transactionId: {}", donation.getTransactionId());
                return;
            }

            String subject = "Donation Receipt - " + donation.getTransactionId();
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

            logger.info("Receipt sent successfully for transactionId: {}, to email: {}", donation.getTransactionId(), donor.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send receipt for transactionId: {}", donation.getTransactionId(), e);
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
