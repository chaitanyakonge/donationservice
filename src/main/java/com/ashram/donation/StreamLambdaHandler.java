package com.ashram.donation;

import com.amazonaws.serverless.proxy.jersey.JerseyLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.ashram.donation.config.JacksonConfig;
import com.ashram.donation.controller.DonationController;
import com.ashram.donation.controller.DonorController;
import com.ashram.donation.controller.HealthController;
import com.ashram.donation.exception.GlobalExceptionHandler;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {
    private static final JerseyLambdaContainerHandler<?, ?> handler;

    static {
        // Initialize dependency injection component
        final DonationAppComponent component = DaggerDonationAppComponent.create();

        // Configure Jersey ResourceConfig
        final ResourceConfig config = new ResourceConfig()
                .registerInstances(
                        component.donationController(),
                        component.donorController()
                )
                .register(HealthController.class)
                .register(JacksonFeature.class)
                .register(JacksonConfig.class)
                .register(GlobalExceptionHandler.class);

        handler = JerseyLambdaContainerHandler.getHttpApiV2ProxyHandler(config);
    }

    @Override
    public void handleRequest(final InputStream input, final OutputStream output, final Context context) throws IOException {
        context.getLogger().log("Lambda invoked");
        handler.proxyStream(input, output, context);
    }
}