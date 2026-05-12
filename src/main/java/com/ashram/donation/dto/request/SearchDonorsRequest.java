package com.ashram.donation.dto.request;

import com.ashram.donation.dto.PaginationCriteria;
import lombok.Data;

@Data
public class SearchDonorsRequest {
    private String mobile;
    private String name;
    private PaginationCriteria pagination;
}
