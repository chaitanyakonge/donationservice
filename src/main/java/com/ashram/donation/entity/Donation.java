package com.ashram.donation.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Donation {

    private String pk;
    private String transactionId;
    private String donorId;
    private Double amount;
    private String paymentMode;         // stored as String e.g. "UPI", "CASH"
    private String bankReferenceNumber;
    private String status;              // stored as String e.g. "SUCCESS", "PENDING"
    private Long transactionEpoch;
    private String monthPartition;
    private String eventDescription;
    private Boolean receiptGenerated;
    private Boolean isDeleted;
    private Long createdAt;
    private Long updatedAt;
    private Long deletedAt;
    private String deletedBy;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() { return pk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "donorId-index")
    public String getDonorId() { return donorId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "monthPartition-index")
    public String getMonthPartition() { return monthPartition; }

    @DynamoDbSecondarySortKey(indexNames = {"donorId-index", "monthPartition-index"})
    public Long getTransactionEpoch() { return transactionEpoch; }
}
