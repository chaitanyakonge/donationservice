package com.ashram.donation;

import com.ashram.donation.controller.DonationController;
import com.ashram.donation.controller.DonorController;
import com.ashram.donation.module.DatabaseModule;
import com.ashram.donation.module.DonationModule;
import com.ashram.donation.module.DonorModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DatabaseModule.class, DonationModule.class, DonorModule.class})
public interface DonationAppComponent {
    DonationController donationController();
    DonorController donorController();
}