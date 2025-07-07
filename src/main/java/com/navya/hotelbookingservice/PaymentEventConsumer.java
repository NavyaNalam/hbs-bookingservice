package com.navya.hotelbookingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {
    private final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    @Autowired
    private BookingService bookingService;

    @KafkaListener(topics = "payment-events", groupId = "booking-payment-manager-group")
    public void consumePaymentEvent(String message) {
        logger.info("Consumed payment event {}", message);

        ObjectMapper mapper = new ObjectMapper();
        try{
            PaymentEvent paymentEvent = mapper.readValue(message, PaymentEvent.class);
            this.processPaymentEvent(paymentEvent);
        } catch (Exception e) {
            logger.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }

    private void processPaymentEvent(PaymentEvent paymentEvent) {

        if (paymentEvent.getStatus().contentEquals("SUCCESS")) {
            logger.info("Payment processed successfully for booking ID: {}", paymentEvent.getBookingId());
            // Here you can add logic to update the ticket status or notify the user
            bookingService.confirmBooking(paymentEvent.getBookingId());
        } else if (paymentEvent.getStatus().contentEquals("FAILURE")) {
            logger.error("Payment processing failed for booking ID: {}", paymentEvent.getBookingId());
            // Here you can add logic to handle payment failure, e.g., notify the user or retry
            bookingService.cancelBooking(paymentEvent.getBookingId());
        } else {
            logger.warn("Received unknown payment status for booking ID: {}", paymentEvent.getBookingId());

        }
    }
}