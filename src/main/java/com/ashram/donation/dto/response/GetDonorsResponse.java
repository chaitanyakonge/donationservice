package com.ashram.donation.dto.response;

import com.ashram.donation.dto.AcknowledgementPojo;
import com.ashram.donation.dto.PaginationCriteria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDonorsResponse {
    private List<GetDonorResponse> donors;
    private PaginationCriteria pagination;
    private AcknowledgementPojo acknowledgement;
}
