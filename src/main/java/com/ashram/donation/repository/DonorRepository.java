package com.ashram.donation.repository;

import com.ashram.donation.entity.Donor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;

public class DonorRepository {

    private final DynamoDbTable<Donor> donorTable;

    public DonorRepository(DynamoDbTable<Donor> donorTable) {
        this.donorTable = donorTable;
    }

    public void save(Donor donor) {
        donorTable.putItem(donor);
    }

    public Optional<Donor> findByPk(String pk) {
        return Optional.ofNullable(donorTable.getItem(Key.builder().partitionValue(pk).build()));
    }

    public List<Donor> findByContactNumber(String contactNumber) {
        DynamoDbIndex<Donor> index = donorTable.index("contactNumber-index");
        return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(contactNumber).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public List<Donor> findByFullName(String fullName) {
        DynamoDbIndex<Donor> index = donorTable.index("fullName-index");
        return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(fullName.toLowerCase()).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public List<Donor> findAll() {
        return donorTable.scan(ScanEnhancedRequest.builder().build())
                .stream()
                .flatMap(page -> page.items().stream())
                .filter(donor -> !Boolean.TRUE.equals(donor.getIsDeleted()))
                .toList();
    }
}
