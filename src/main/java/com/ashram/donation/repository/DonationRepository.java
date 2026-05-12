package com.ashram.donation.repository;

import com.ashram.donation.entity.Donation;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
public class DonationRepository {

    private final DynamoDbTable<Donation> donationTable;

    public DonationRepository(DynamoDbTable<Donation> donationTable) {
        this.donationTable = donationTable;
    }

    public void save(Donation donation) {
        donationTable.putItem(donation);
    }

    public Optional<Donation> findByPk(String pk) {
        return Optional.ofNullable(donationTable.getItem(Key.builder().partitionValue(pk).build()));
    }

    public List<Donation> findByDonorId(String donorId) {
        DynamoDbIndex<Donation> index = donationTable.index("donorId-index");
        return index.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(donorId).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public List<Donation> findByMonthPartitionAndEpochRange(String monthPartition, Long startEpoch, Long endEpoch) {
        DynamoDbIndex<Donation> index = donationTable.index("monthPartition-index");
        return index.query(QueryConditional.sortBetween(
                        Key.builder().partitionValue(monthPartition).sortValue(startEpoch).build(),
                        Key.builder().partitionValue(monthPartition).sortValue(endEpoch).build()))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }
}
