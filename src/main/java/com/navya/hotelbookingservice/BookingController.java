package com.navya.hotelbookingservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    BookingService bookingService;

    @Autowired
    TokenService tokenService;

    @GetMapping("bookings/{userId}")
    public ResponseEntity<?> getAllBookingsByUserId(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        String phone = null;
        try {
            phone = tokenService.validateToken(token);
        } catch (WebClientResponseException e) {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }
        if (phone.isEmpty()) {
            logger.info("Token validation failed: Phone number is empty");
            return ResponseEntity.status(401).body("Token Not Found");
        }

        if(!phone.equals(userId))
        {
            logger.info("Phone number mismatch");
            return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
        }

        logger.info("User Fetched Bookings Successfully");
        return ResponseEntity.ok("Bookings Fetched Successfully" + bookingService.findBookingsByUserId(userId));
    }

    @GetMapping("allbookings/{userId}")
    public ResponseEntity<?> getAllBookingsByAdmin(@RequestHeader("Authorization") String token, @PathVariable String userId) {
        String phone = null;
        try {
            phone = tokenService.validateToken(token);
        } catch (WebClientResponseException e) {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }
        if (phone.isEmpty()) {
            logger.info("Token validation failed: Phone number is empty");
            return ResponseEntity.status(401).body("Token Not Found");
        }

        if(!phone.equals(userId))
        {
            logger.info("Phone number mismatch");
            return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
        }

        String role = tokenService.getRoleFromToken(token);

        if(role.isEmpty()){
            logger.info("Token validation failed: Role is empty");
            return ResponseEntity.status(401).body("Invalid token or role not found");
        }
        else if(role.equals("ADMIN")) {
            logger.info("All Bookings fetched Successfully");
            return ResponseEntity.ok("Bookings Fetched Successfully" + bookingService.findAll());
        }
        else{
            logger.info("Unauthorized access: User is not an admin");
            return ResponseEntity.status(403).body("Access Denied: Only admins can view all bookings");
        }
    }

    @PostMapping("/book/")
    public ResponseEntity<String> addBooking(@RequestBody RequestBooking bookingRequest, @RequestHeader("Authorization") String token) {
        String phone = null;
        try {
            phone = tokenService.validateToken(token);
        } catch (WebClientResponseException e) {
            logger.info("Token validation failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid token");
        }
        if (phone.isEmpty()) {
            logger.info("Token validation failed: Phone number is empty");
            return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
        } else if (!phone.equals(bookingRequest.getUserId())) {
            logger.info("Token validation failed: Phone number mismatch");
            return ResponseEntity.status(401).body("Invalid token or phone number mismatch");
        } else {
            logger.info("Token validation successful: Phone number matches");
            logger.info("Inside addBooking of BookingController");

            Booking newBooking = new Booking();
            newBooking.setHotelName(bookingRequest.getHotelName());
            newBooking.setUserId(bookingRequest.getUserId());
            newBooking.setNumOfRoomsBooked(bookingRequest.getNumOfRooms());
            newBooking.setStartDate(bookingRequest.getCheckInDate());
            newBooking.setEndDate(bookingRequest.getCheckOutDate());

            return (ResponseEntity<String>) bookingService.createBooking(newBooking);
        }
    }

        @GetMapping("getbooking/{id}")
        public ResponseEntity<?> findABooking (@PathVariable("id") String id){
            logger.info("Inside findABooking of BookingController");
            return ResponseEntity.ok(bookingService.findBookingById(id));
        }




}


