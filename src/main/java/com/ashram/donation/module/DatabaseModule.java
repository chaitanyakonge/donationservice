package com.ashram.donation.module;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Singleton;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DynamoDbClient provideDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION_OVERRIDE", "ap-south-1")))
                .build();
    }

    @Provides
    @Singleton
    public DynamoDbEnhancedClient provideDynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Provides
    @Singleton
    public DynamoDbTable<Donor> provideDonorTable(DynamoDbEnhancedClient enhancedClient) {
        String tableName = System.getenv().getOrDefault("AWS_DYNAMODB_TABLES_DONORS", "ngo-donors");
        return enhancedClient.table(tableName, TableSchema.fromBean(Donor.class));
    }

    @Provides
    @Singleton
    public DynamoDbTable<Donation> provideDonationTable(DynamoDbEnhancedClient enhancedClient) {
        String tableName = System.getenv().getOrDefault("AWS_DYNAMODB_TABLES_DONATIONS", "ngo-donations");
        return enhancedClient.table(tableName, TableSchema.fromBean(Donation.class));
    }
}