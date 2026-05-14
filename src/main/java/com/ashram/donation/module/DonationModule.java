package com.ashram.donation.module;

import com.ashram.donation.entity.Donation;
import com.ashram.donation.handler.DonationHandler;
import com.ashram.donation.repository.DonationRepository;
import com.ashram.donation.repository.DonorRepository;
import com.ashram.donation.service.DonationService;
import com.ashram.donation.service.DonorService;
import com.ashram.donation.service.NotificationService;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import javax.inject.Singleton;

@Module
public class DonationModule {

    @Provides
    @Singleton
    public DonationService provideDonationService(DonationRepository donationRepository) {
        return new DonationService(donationRepository);
    }

    @Provides
    @Singleton
    public DonationHandler provideDonationHandler(
            DonorService donorService,
            DonationService donationService,
            NotificationService notificationService) {
        return new DonationHandler(donorService, donationService, notificationService);
    }

    @Provides
    @Singleton
    public DonationRepository provideDonationRepository(DynamoDbTable<Donation> donationTable) {
        return new DonationRepository(donationTable);
    }
}