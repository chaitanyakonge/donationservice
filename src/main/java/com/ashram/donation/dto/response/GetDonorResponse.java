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
public class GetDonorResponse {
    private String donorId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String email;
    private String panNumber;
    private String address;
    private Long createdAt;
    private AcknowledgementPojo acknowledgement;
}
