package com.ashram.donation.dto.response;

import com.ashram.donation.dto.AcknowledgementPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessUnifiedDonationResponse {
    private String transactionId;
    private AcknowledgementPojo acknowledgement;
}
