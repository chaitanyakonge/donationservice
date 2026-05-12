package com.ashram.donation.dto.request;

import com.ashram.donation.dto.PaginationCriteria;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetDonationHistoryForDonorRequest {

    @NotBlank
    private String donorId;

    private PaginationCriteria pagination;
}
