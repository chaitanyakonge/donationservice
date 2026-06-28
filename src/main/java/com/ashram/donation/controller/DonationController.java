package com.ashram.donation.controller;

import com.ashram.donation.dto.AcknowledgementPojo;
import com.ashram.donation.dto.ControllerUrls;
import com.ashram.donation.dto.request.*;
import com.ashram.donation.dto.response.*;
import com.ashram.donation.entity.Donation;
import com.ashram.donation.entity.Donor;
import com.ashram.donation.handler.DonationHandler;
import com.ashram.donation.service.DonationService;
import com.ashram.donation.service.DonorService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Slf4j
@Singleton
@Path(ControllerUrls.DONATION_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DonationController {

    private final DonationService donationService;
    private final DonationHandler donationHandler;
    private final DonorService donorService;

    @Inject
    public DonationController(@NonNull final DonationService donationService, @NonNull final DonationHandler donationHandler, @NonNull final DonorService donorService) {
        this.donationService = donationService;
        this.donationHandler = donationHandler;
        this.donorService = donorService;
    }

    @POST
    @Path(ControllerUrls.PROCESS_UNIFIED_DONATION)
    public Response processUnifiedDonation(ProcessUnifiedDonationRequest request) {
        log.info("Welcome to processUnifiedDonation, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ProcessUnifiedDonationResponse(null, new AcknowledgementPojo(false, "Bad request, please verify your request")))
                    .build();
        }
        ProcessUnifiedDonationResponse response;
        try {
            String transactionId = donationHandler.processUnifiedDonation(request);
            response = new ProcessUnifiedDonationResponse(transactionId, new AcknowledgementPojo(true, "Donation processed successfully"));
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while processing unified donation, request: {}", request, e);
            response = new ProcessUnifiedDonationResponse(null, new AcknowledgementPojo(false, "Something wrong happened on the server"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }

    @POST
    @Path(ControllerUrls.GET_DONATION_BY_ID)
    public Response getDonationById(GetDonationByIdRequest request) {
        log.info("Welcome to getDonationById, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            GetDonationResponse response = toResponse(donationService.getDonationById(request.getTransactionId()));
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving donation by id, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.UPDATE_TRANSACTION_STATUS)
    public Response updateTransactionStatus(UpdateTransactionStatusRequest request) {
        log.info("Welcome to updateTransactionStatus, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            donationService.updateTransactionStatus(request.getTransactionId(), request.getStatus(), request.getUtr());
            return Response.ok(GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Transaction status updated successfully"))
                    .build()).build();
        } catch (Exception e) {
            log.error("Error occurred while updating transaction status, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.GET_DONATION_HISTORY_FOR_DONOR)
    public Response getDonationHistoryForDonor(GetDonationHistoryForDonorRequest request) {
        log.info("Welcome to getDonationHistoryForDonor, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationsResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            GetDonationsResponse response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationHistoryForDonor(request.getDonorId()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donation history fetched successfully"))
                    .build();
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving donation history, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationsResponse.builder()
                            .donations(new ArrayList<>())
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.GET_DONATIONS_IN_RANGE)
    public Response getDonationsInRange(GetDonationsInRangeRequest request) {
        log.info("Welcome to getDonationsInRange, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationsResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            GetDonationsResponse response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationsInRange(request.getStartEpoch(), request.getEndEpoch()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donations fetched successfully"))
                    .build();
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving donations in range, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationsResponse.builder()
                            .donations(new ArrayList<>())
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.CALCULATE_TOTAL_DONATIONS_IN_RANGE)
    public Response calculateTotalDonationsInRange(GetDonationsInRangeRequest request) {
        log.info("Welcome to calculateTotalDonationsInRange, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new CalculateTotalDonationsInRangeResponse(null, new AcknowledgementPojo(false, "Bad request, please verify your request")))
                    .build();
        }
        try {
            Double total = donationService.calculateTotalDonationsInRange(request.getStartEpoch(), request.getEndEpoch());
            CalculateTotalDonationsInRangeResponse response = new CalculateTotalDonationsInRangeResponse(total, new AcknowledgementPojo(true, "Total calculated successfully"));
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while calculating total donations in range, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new CalculateTotalDonationsInRangeResponse(null, new AcknowledgementPojo(false, "Something wrong happened on the server")))
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.GET_DONATIONS_BY_PAYMENT_MODE)
    public Response getDonationsByPaymentMode(GetDonationsByPaymentModeRequest request) {
        log.info("Welcome to getDonationsByPaymentMode, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationsResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            GetDonationsResponse response = GetDonationsResponse.builder()
                    .donations(donationService.getDonationsInRangeByPaymentMode(request.getStartEpoch(), request.getEndEpoch(), request.getPaymentMode().name()).stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donations fetched successfully"))
                    .build();
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving donations by payment mode, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationsResponse.builder()
                            .donations(new ArrayList<>())
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.SOFT_DELETE_DONATION)
    public Response softDeleteDonation(SoftDeleteDonationRequest request) {
        log.info("Welcome to softDeleteDonation, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            donationService.softDeleteDonation(request.getTransactionId(), request.getDeletedBy());
            return Response.ok(GetDonationResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Donation deleted successfully"))
                    .build()).build();
        } catch (Exception e) {
            log.error("Error occurred while deleting donation, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonationResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    private GetDonationResponse toResponse(Donation donation) {
        return GetDonationResponse.builder()
                .transactionId(donation.getTransactionId())
                .donorId(donation.getDonorId())
                .donorName(resolveDonorName(donation.getDonorId()))
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

    private String resolveDonorName(String donorId) {
        if (donorId == null) {
            return null;
        }
        try {
            Donor donor = donorService.getDonorById(donorId);
            return (donor.getFirstName() + " " + donor.getLastName()).trim();
        } catch (Exception e) {
            log.warn("Could not resolve donor name for donorId: {}", donorId);
            return null;
        }
    }
}
