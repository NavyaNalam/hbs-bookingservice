package com.navya.hotelbookingservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    BookingService bookingService;

    @GetMapping("bookings")
    public List<Booking> getAllBookings() {
        logger.info("Inside getAllBookings of BookingController");
        return bookingService.findAll();
    }

    @PostMapping("/add-booking/{hotel-id}")
    public ResponseEntity<String> addBooking(@PathVariable("hotel-id") Long hotelId, @RequestBody Booking booking) {
        logger.info("Inside addBooking of BookingController");
        Object hasNewBooking = bookingService.saveBooking(hotelId, booking);
        if ((boolean) hasNewBooking) {
            return ResponseEntity.ok("Hotel Booked Successfully");
        } else {
            return ResponseEntity.badRequest().body("Could not Book Hotel. Please try again!");
        }
    }


    @GetMapping("/{id}")
    public Booking findABooking(@PathVariable("id") Long id) {
        logger.info("Inside findABooking of BookingController");
        return bookingService.findBookingById(id);
    }

/*    @GetMapping("/user-bookings/{user-id}")
    public ResponseEntity<List<Hotel>> getUserBookedHotels(@PathVariable("user-id") Long userId) {
        logger.info("Inside getUserBookedHotels of BookingController");
        return ResponseEntity.ok(bookingService.getUserBookedHotels(userId));

    }*/
}
