package com.ashram.donation.dto.request;

import com.ashram.donation.dto.PaginationCriteria;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetDonationsInRangeRequest {

    @NotNull
    private Long startEpoch;

    @NotNull
    private Long endEpoch;

    private PaginationCriteria pagination;
}
