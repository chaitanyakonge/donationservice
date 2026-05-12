package com.ashram.donation.controller;

import com.ashram.donation.dto.AcknowledgementPojo;
import com.ashram.donation.dto.ControllerUrls;
import com.ashram.donation.dto.request.*;
import com.ashram.donation.dto.response.*;
import com.ashram.donation.entity.Donation;
import com.ashram.donation.handler.DonationHandler;
import com.ashram.donation.service.DonationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Validated
@RestController
@RequestMapping(ControllerUrls.DONATION_BASE)
public class DonationController {

    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    private final DonationService donationService;
    private final DonationHandler donationHandler;

    public DonationController(DonationService donationService, DonationHandler donationHandler) {
        this.donationService = donationService;
        this.donationHandler = donationHandler;
    }

    @PostMapping(ControllerUrls.PROCESS_UNIFIED_DONATION)
    public ProcessUnifiedDonationResponse processUnifiedDonation(@RequestBody ProcessUnifiedDonationRequest request) {
        logger.info("Welcome to processUnifiedDonation, request: {}", request);
        if (request == null) {
            return new ProcessUnifiedDonationResponse(null, new AcknowledgementPojo(false, "Bad request, please verify your request"));
        }
        ProcessUnifiedDonationResponse response;
        try {
            String transactionId = donationHandler.processUnifiedDonation(request);
            response = new ProcessUnifiedDonationResponse(transactionId, new AcknowledgementPojo(true, "Donation processed successfully"));
        } catch (Exception e) {
            logger.error("Error occurred while processing unified donation, request: {}", request);
            response = new ProcessUnifiedDonationResponse(null, new AcknowledgementPojo(false, "Something wrong happened on the server"));
        }
        return response;
    }

    @PostMapping(ControllerUrls.GET_DONATION_BY_ID)
    public GetDonationResponse getDonationById(@RequestBody GetDonationByIdRequest request) {
        logger.info("Welcome to getDonationById, request: {}", request);
        if (request == null) {
            return GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationResponse response;
        try {
            response = toResponse(donationService.getDonationById(request.getTransactionId()));
        } catch (Exception e) {
            logger.error("Error occurred while retrieving donation by id, request: {}", request);
            response = GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.UPDATE_TRANSACTION_STATUS)
    public GetDonationResponse updateTransactionStatus(@RequestBody UpdateTransactionStatusRequest request) {
        logger.info("Welcome to updateTransactionStatus, request: {}", request);
        if (request == null) {
            return GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationResponse response;
        try {
            donationService.updateTransactionStatus(request.getTransactionId(), request.getStatus(), request.getUtr());
            response = GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Transaction status updated successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while updating transaction status, request: {}", request);
            response = GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.GET_DONATION_HISTORY_FOR_DONOR)
    public GetDonationsResponse getDonationHistoryForDonor(@RequestBody GetDonationHistoryForDonorRequest request) {
        logger.info("Welcome to getDonationHistoryForDonor, request: {}", request);
        if (request == null) {
            return GetDonationsResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationsResponse response;
        try {
            response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationHistoryForDonor(request.getDonorId()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donation history fetched successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving donation history, request: {}", request);
            response = GetDonationsResponse.builder()
                    .donations(new ArrayList<>())
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.GET_DONATIONS_IN_RANGE)
    public GetDonationsResponse getDonationsInRange(@RequestBody GetDonationsInRangeRequest request) {
        logger.info("Welcome to getDonationsInRange, request: {}", request);
        if (request == null) {
            return GetDonationsResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationsResponse response;
        try {
            response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationsInRange(request.getStartEpoch(), request.getEndEpoch()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donations fetched successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving donations in range, request: {}", request);
            response = GetDonationsResponse.builder()
                    .donations(new ArrayList<>())
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.CALCULATE_TOTAL_DONATIONS_IN_RANGE)
    public CalculateTotalDonationsInRangeResponse calculateTotalDonationsInRange(@RequestBody GetDonationsInRangeRequest request) {
        logger.info("Welcome to calculateTotalDonationsInRange, request: {}", request);
        if (request == null) {
            return new CalculateTotalDonationsInRangeResponse(null, new AcknowledgementPojo(false, "Bad request, please verify your request"));
        }
        CalculateTotalDonationsInRangeResponse response;
        try {
            Double total = donationService.calculateTotalDonationsInRange(request.getStartEpoch(), request.getEndEpoch());
            response = new CalculateTotalDonationsInRangeResponse(total, new AcknowledgementPojo(true, "Total calculated successfully"));
        } catch (Exception e) {
            logger.error("Error occurred while calculating total donations in range, request: {}", request);
            response = new CalculateTotalDonationsInRangeResponse(null, new AcknowledgementPojo(false, "Something wrong happened on the server"));
        }
        return response;
    }

    @PostMapping(ControllerUrls.GET_DONATIONS_BY_PAYMENT_MODE)
    public GetDonationsResponse getDonationsByPaymentMode(@RequestBody GetDonationsByPaymentModeRequest request) {
        logger.info("Welcome to getDonationsByPaymentMode, request: {}", request);
        if (request == null) {
            return GetDonationsResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationsResponse response;
        try {
            response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationsInRangeByPaymentMode(request.getStartEpoch(), request.getEndEpoch(), request.getPaymentMode().name()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donations fetched successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving donations by payment mode, request: {}", request);
            response = GetDonationsResponse.builder()
                    .donations(new ArrayList<>())
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    @PostMapping(ControllerUrls.SOFT_DELETE_DONATION)
    public GetDonationResponse softDeleteDonation(@RequestBody SoftDeleteDonationRequest request) {
        logger.info("Welcome to softDeleteDonation, request: {}", request);
        if (request == null) {
            return GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                    .build();
        }
        GetDonationResponse response;
        try {
            donationService.softDeleteDonation(request.getTransactionId(), request.getDeletedBy());
            response = GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Donation deleted successfully"))
                    .build();
        } catch (Exception e) {
            logger.error("Error occurred while deleting donation, request: {}", request);
            response = GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                    .build();
        }
        return response;
    }

    private GetDonationResponse toResponse(Donation donation) {
        return GetDonationResponse.builder()
                .transactionId(donation.getTransactionId())
                .donorId(donation.getDonorId())
                .amount(donation.getAmount())
                .paymentMode(donation.getPaymentMode())
                .bankReferenceNumber(donation.getBankReferenceNumber())
                .status(donation.getStatus())
                .transactionEpoch(donation.getTransactionEpoch())
                .eventDescription(donation.getEventDescription())
                .receiptGenerated(donation.getReceiptGenerated())
                .acknowledgement(new AcknowledgementPojo(true, "Success"))
                .build();
    }
}
