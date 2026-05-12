package com.ashram.donation.dto;

public class ControllerUrls {

    private ControllerUrls() {}

    // Base URLs
    public static final String DONOR_BASE = "/api/donors";
    public static final String DONATION_BASE = "/api/donations";

    // Donor URLs
    public static final String GET_DONOR_BY_ID = "/getdonorbyid";
    public static final String SEARCH_DONORS = "/searchdonors";
    public static final String SOFT_DELETE_DONOR = "/softdeletedonor";

    // Donation URLs
    public static final String PROCESS_UNIFIED_DONATION = "/processunifieddonation";
    public static final String GET_DONATION_BY_ID = "/getdonationbyid";
    public static final String UPDATE_TRANSACTION_STATUS = "/updatetransactionstatus";
    public static final String GET_DONATION_HISTORY_FOR_DONOR = "/getdonationhistoryfordonor";
    public static final String GET_DONATIONS_IN_RANGE = "/getdonationsinrange";
    public static final String CALCULATE_TOTAL_DONATIONS_IN_RANGE = "/calculatetotaldonationsinrange";
    public static final String GET_DONATIONS_BY_PAYMENT_MODE = "/getdonationsbypaymentmode";
    public static final String SOFT_DELETE_DONATION = "/softdeletedonation";
}
