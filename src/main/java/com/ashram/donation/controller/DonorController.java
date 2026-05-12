package com.ashram.donation.controller;

import com.ashram.donation.dto.AcknowledgementPojo;
import com.ashram.donation.dto.ControllerUrls;
import com.ashram.donation.dto.request.GetDonorByIdRequest;
import com.ashram.donation.dto.request.SearchDonorsRequest;
import com.ashram.donation.dto.request.SoftDeleteDonorRequest;
import com.ashram.donation.dto.response.GetDonorResponse;
import com.ashram.donation.dto.response.GetDonorsResponse;
import com.ashram.donation.entity.Donor;
import com.ashram.donation.service.DonorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Validated
@RestController
@RequestMapping(ControllerUrls.DONOR_BASE)
public class DonorController {

    private static final Logger logger = LoggerFactory.getLogger(DonorController.class);

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @PostMapping(ControllerUrls.GET_DONOR_BY_ID)
    public GetDonorResponse getDonorById(@RequestBody GetDonorByIdRequest request) {
        logger.info("Welcome to getDonorById, request: {}", request);
        if (request == null) {
            return GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonorResponse response;
        try {
            response = toResponse(donorService.getDonorById(request.getDonorId()));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving donor by id, request: {}", request);
            response = GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.SEARCH_DONORS)
    public GetDonorsResponse searchDonors(@RequestBody SearchDonorsRequest request) {
        logger.info("Welcome to searchDonors, request: {}", request);
        if (request == null) {
            return GetDonorsResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonorsResponse response;
        try {
            if (request.getMobile() == null && request.getName() == null) {
                return GetDonorsResponse.builder()
                        .acknowledgement(new AcknowledgementPojo(false, "Provide either mobile or name in request"))
                        .build();
            }
            var donors = request.getMobile() != null
                    ? donorService.searchByContactNumber(request.getMobile())
                    : donorService.searchByName(request.getName());
            response = GetDonorsResponse.builder()
                    .donors(donors.stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donors fetched successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while searching donors, request: {}", request);
            response = GetDonorsResponse.builder()
                    .donors(new ArrayList<>())
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.SOFT_DELETE_DONOR)
    public GetDonorResponse softDeleteDonor(@RequestBody SoftDeleteDonorRequest request) {
        logger.info("Welcome to softDeleteDonor, request: {}", request);
        if (request == null) {
            return GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonorResponse response;
        try {
            donorService.softDeleteDonor(request.getDonorId(), request.getDeletedBy());
            response = GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Donor deleted successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting donor, request: {}", request);
            response = GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    private GetDonorResponse toResponse(Donor donor) {
        return GetDonorResponse.builder()
                .donorId(donor.getDonorId())
                .firstName(donor.getFirstName())
                .lastName(donor.getLastName())
                .contactNumber(donor.getContactNumber())
                .email(donor.getEmail())
                .panNumber(donor.getPanNumber())
                .address(donor.getAddress())
                .createdAt(donor.getCreatedAt())
                .acknowledgement(new AcknowledgementPojo(true, "Success"))
                .build();
    }
}
