package com.ashram.donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationCriteria {
    private int size;
    private String lastEvaluatedKey;
}
