package com.ashram.donation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SoftDeleteDonorRequest {

    @NotBlank
    private String donorId;

    @NotBlank
    private String deletedBy;
}
