package com.ashram.donation.config;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamodb.tables.donors}")
    private String donorsTable;

    @Value("${aws.dynamodb.tables.donations}")
    private String donationsTable;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<Donor> donorTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(donorsTable, TableSchema.fromBean(Donor.class));
    }

    @Bean
    public DynamoDbTable<Donation> donationTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(donationsTable, TableSchema.fromBean(Donation.class));
    }

    @Bean
    public SesClient sesClient() {
        return SesClient.builder()
                .region(Region.of(region))
                .build();
    }
}
