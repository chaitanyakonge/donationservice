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
@Path(ControllerUrls.DONOR_BASE)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DonorController {

    private final DonorService donorService;

    @Inject
    public DonorController(@NonNull final DonorService donorService) {
        this.donorService = donorService;
    }

    @POST
    @Path(ControllerUrls.GET_DONOR_BY_ID)
    public Response getDonorById(GetDonorByIdRequest request) {
        log.info("Welcome to getDonorById, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonorResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            GetDonorResponse response = toResponse(donorService.getDonorById(request.getDonorId()));
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving donor by id, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonorResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.SEARCH_DONORS)
    public Response searchDonors(SearchDonorsRequest request) {
        log.info("Welcome to searchDonors, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonorsResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            if (request.getMobile() == null && request.getName() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(GetDonorsResponse.builder()
                                .acknowledgement(new AcknowledgementPojo(false, "Provide either mobile or name in request"))
                                .build())
                        .build();
            }
            var donors = request.getMobile() != null
                    ? donorService.searchByContactNumber(request.getMobile())
                    : donorService.searchByName(request.getName());
            GetDonorsResponse response = GetDonorsResponse.builder()
                    .donors(donors.stream().map(this::toResponse).toList())
                    .acknowledgement(new AcknowledgementPojo(true, "Donors fetched successfully"))
                    .build();
            return Response.ok(response).build();
        } catch (Exception e) {
            log.error("Error occurred while searching donors, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonorsResponse.builder()
                            .donors(new ArrayList<>())
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
    }

    @POST
    @Path(ControllerUrls.SOFT_DELETE_DONOR)
    public Response softDeleteDonor(SoftDeleteDonorRequest request) {
        log.info("Welcome to softDeleteDonor, request: {}", request);
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(GetDonorResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Bad request, please verify your request"))
                            .build())
                    .build();
        }
        try {
            donorService.softDeleteDonor(request.getDonorId(), request.getDeletedBy());
            return Response.ok(GetDonorResponse.builder()
                    .acknowledgement(new AcknowledgementPojo(true, "Donor deleted successfully"))
                    .build()).build();
        } catch (Exception e) {
            log.error("Error occurred while deleting donor, request: {}", request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(GetDonorResponse.builder()
                            .acknowledgement(new AcknowledgementPojo(false, "Something wrong happened on the server"))
                            .build())
                    .build();
        }
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
