package com.ashram.donation.dto.response;

import com.ashram.donation.dto.AcknowledgementPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDonationResponse {
    private String transactionId;
    private String donorId;
    private Double amount;
    private String paymentMode;
    private String bankReferenceNumber;
    private String status;
    private Long transactionEpoch;
    private String eventDescription;
    private Boolean receiptGenerated;
    private AcknowledgementPojo acknowledgement;
}
