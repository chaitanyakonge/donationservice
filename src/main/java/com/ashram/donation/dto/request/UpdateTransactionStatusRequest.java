package com.ashram.donation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTransactionStatusRequest {

    @NotBlank
    private String transactionId;

    @NotBlank
    private String status;

    private String utr;
}
