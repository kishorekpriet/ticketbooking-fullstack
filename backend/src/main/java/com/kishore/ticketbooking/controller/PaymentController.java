package com.kishore.ticketbooking.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*") // Allow React to talk to this endpoint
public class PaymentController {

    // This grabs the secret key we just put in your application.properties file!
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody PaymentRequest request) {
        // 1. Give Stripe our secret password
        Stripe.apiKey = stripeApiKey;

        try {
            // 2. Build the digital invoice
            SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // Where Stripe sends the user if they successfully pay
                .setSuccessUrl("http://localhost:5173/?payment=success") 
                // Where Stripe sends them if they click the back button
                .setCancelUrl("http://localhost:5173/?payment=cancelled") 
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("inr") // Indian Rupees!
                                // Stripe calculates in paise (cents), so we multiply the total by 100
                                .setUnitAmount(request.getAmount() * 100L) 
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("BookMyShow - Movie Tickets")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build();

            // 3. Send the invoice to Stripe, and Stripe gives us back a special Checkout URL
            Session session = Session.create(params);

            // 4. Send that URL back to React so React can redirect the user to the payment screen
            Map<String, String> responseData = new HashMap<>();
            responseData.put("checkoutUrl", session.getUrl());
            
            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating payment session: " + e.getMessage());
        }
    }

    // A tiny DTO to catch the total amount sent by React
    public static class PaymentRequest {
        private Long amount;
        public Long getAmount() { return amount; }
        public void setAmount(Long amount) { this.amount = amount; }
    }
}