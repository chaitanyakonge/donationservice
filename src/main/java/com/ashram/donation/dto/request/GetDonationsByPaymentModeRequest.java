package com.ashram.donation.dto.request;

import com.ashram.donation.dto.PaginationCriteria;
import com.ashram.donation.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetDonationsByPaymentModeRequest {

    @NotNull
    private Long startEpoch;

    @NotNull
    private Long endEpoch;

    @NotNull
    private PaymentMode paymentMode;

    private PaginationCriteria pagination;
}
