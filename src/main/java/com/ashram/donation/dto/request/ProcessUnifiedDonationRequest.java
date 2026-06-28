package com.ashram.donation.dto.request;

import com.ashram.donation.enums.DonationStatus;
import com.ashram.donation.enums.PaymentMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProcessUnifiedDonationRequest {

    // Donor fields
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String contactNumber;

    private String email;
    private String panNumber;
    private String address;

    // Donation fields
    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private PaymentMode paymentMode;

    private String bankReferenceNumber;
    private String eventDescription;

    @NotNull
    private DonationStatus status;
}
