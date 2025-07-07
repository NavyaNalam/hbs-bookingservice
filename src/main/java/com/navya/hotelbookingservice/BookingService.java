package com.navya.hotelbookingservice;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    WebClient webClientBuilder;

    @Autowired
    private HotelInventoryService hotelInventoryService;

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public boolean createBooking(Booking booking) {
        logger.info("Creating Booking for Hotel: " + booking.getHotelName() + " with User ID: " + booking.getUserId());

        //Get the Hotel from the Booking
        Optional<Hotel> hotel = hotelRepository.findByHotelName(booking.getHotelName());
        if (hotel.isEmpty()) {
            logger.debug("Hotel Inventory doesnt exist for Hotel Name: " + booking.getHotelName());
            return false;
        } else {
            int bookedRooms = booking.getNumOfRoomsBooked();
            int numOfRoomsAvailable = hotel.get().getNumOfRoomsAvailable();

            // Check to see if there are available rooms
            if (numOfRoomsAvailable <= 0 || bookedRooms > numOfRoomsAvailable) {
                return false;
            } else {

                // update the hotel number of rooms available
                int numOfHotelRoomRemaining = numOfRoomsAvailable - bookedRooms;
                int updatedRow;
                updatedRow = hotelInventoryService.updateNumOfRoomsAvailable(booking.getHotelName(), numOfHotelRoomRemaining);

                int i = updatedRow;

                if (i == 0) {
                    return false;
                }

                booking.setBookingStatus("pending");

                //create the booking
                bookingRepository.save(booking);

                //Handle the Payment Service call here

                return true;
            }

        }
    }

    public Optional<Booking> findBookingById(Long id)
    {
        return bookingRepository.findBookingById(id);
    }

    // This should be coming from Payment Service not from user
    @Transactional
    public ResponseEntity<?> confirmBooking(Long bookingId) {
        logger.debug("Confirming Booking: " + bookingId);
        Optional<Booking> existingBooking = bookingRepository.findBookingById(bookingId);
        if (existingBooking.isEmpty()) {
            logger.debug("Booking not found for ID: " + bookingId);
            return ResponseEntity.status(404).body("Booking with ID: " + bookingId + " not found");
        } else {
            Booking booking = existingBooking.get();
            if (!"Pending".equals(booking.getBookingStatus())) {
                logger.debug("Booking is not in pending status: " + booking);
                return ResponseEntity.status(400).body("Reservation is not in pending status");
            }
            booking.setBookingStatus("Confirmed");
            bookingRepository.save(booking); // Save the updated Booking
            logger.debug("Booking confirmed successfully: " + booking);
            return ResponseEntity.ok("Booking confirmed successfully");
        }
    }

    @Transactional
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        logger.debug("Cancelling reservation: " + bookingId);
        Optional<Booking> existingBooking = bookingRepository.findBookingById(bookingId);
        if (existingBooking.isEmpty()) {
            logger.debug("Booking not found for ID: " + bookingId);
            return ResponseEntity.status(404).body("Booking with ID: " + bookingId + " not found");
        } else {
            // Update the inventory
            Booking bookingFound = existingBooking.get();
            Optional<Hotel> hotelOptional = hotelRepository.findByHotelName(bookingFound.getHotelName());

            if (hotelOptional.isEmpty()) {
                logger.debug("Inventory not found for Hotel Name: " + bookingFound.getHotelName());
                return ResponseEntity.status(404).body("Hotel with Hotel Name: " + bookingFound.getHotelName() + " not found");
            }
            Hotel hotel = hotelOptional.get();
            hotel.setNumOfRoomsAvailable(hotel.getNumOfRoomsAvailable() + bookingFound.getNumOfRoomsBooked());


            hotelRepository.save(hotel); // Save the updated inventory

            bookingFound.setBookingStatus("Cancelled");
            // Delete the Booking
            bookingRepository.save(existingBooking.get());
            logger.debug("Booking cancelled successfully: " + bookingFound);
            return ResponseEntity.ok("Booking cancelled successfully");
        }
    }

}





