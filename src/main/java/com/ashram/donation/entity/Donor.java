package com.ashram.donation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Donor {

    private String pk;
    private String donorId;
    private String firstName;
    private String lastName;
    private String fullName;        // system-managed, always lowercase, used only for GSI-2
    private String contactNumber;
    private String email;
    private String panNumber;
    private String address;
    private Boolean isDeleted;
    private Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
    private String deletedBy;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() { return pk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "contactNumber-index")
    public String getContactNumber() { return contactNumber; }

    @DynamoDbSecondaryPartitionKey(indexNames = "fullName-index")
    public String getFullName() { return fullName; }

    @DynamoDbSecondarySortKey(indexNames = {"contactNumber-index", "fullName-index"})
    public Long getCreatedAt() { return createdAt; }
}
