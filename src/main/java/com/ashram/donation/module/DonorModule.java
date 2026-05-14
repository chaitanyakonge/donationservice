package com.ashram.donation.module;

import com.ashram.donation.entity.Donor;
import com.ashram.donation.repository.DonorRepository;
import com.ashram.donation.service.DonorService;
import com.ashram.donation.service.NotificationService;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import javax.inject.Singleton;

@Module
public class DonorModule {

    @Provides
    @Singleton
    public DonorService provideDonorService(DonorRepository donorRepository) {
        return new DonorService(donorRepository);
    }

    @Provides
    @Singleton
    public DonorRepository provideDonorRepository(DynamoDbTable<Donor> donorTable) {
        return new DonorRepository(donorTable);
    }

    @Provides
    @Singleton
    public NotificationService provideNotificationService() {
        return new NotificationService();
    }
}