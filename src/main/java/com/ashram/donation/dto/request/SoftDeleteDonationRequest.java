package com.ashram.donation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SoftDeleteDonationRequest {

    @NotBlank
    private String transactionId;

    @NotBlank
    private String deletedBy;
}
