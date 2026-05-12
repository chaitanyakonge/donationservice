package com.ashram.donation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetDonorByIdRequest {

    @NotBlank
    private String donorId;
}
