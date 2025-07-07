package com.navya.hotelbookingservice;

//import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
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
    private BookingRepository bookingRepo;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    BookingEventProducer bookingEventProducer;

    @Autowired
    WebClient webClientBuilder;

    @Autowired
    private HotelInventoryService hotelInventoryService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Integer amountPerRoom = 1000; // Assuming a fixed amount per room for simplicity

    public List<Booking> findAll() {
        return bookingRepo.findAll();
    }


    public ResponseEntity<?> createBooking(Booking booking) {
        logger.info("Creating Booking for Hotel: " + booking.getHotelName() + " with User ID: " + booking.getUserId());

        //Get the Hotel from the Booking
        Optional<Hotel> hotel = hotelRepository.findByHotelName(booking.getHotelName());
        if (hotel.isEmpty()) {
            logger.debug("Hotel Inventory doesnt exist for Hotel Name: " + booking.getHotelName());
            return ResponseEntity.status(404).body("Hotel Not found for the Hotel Name: " + booking.getHotelName());
        } else {
            int bookedRooms = booking.getNumOfRoomsBooked();
            int numOfRoomsAvailable = hotel.get().getNumOfRoomsAvailable();

            // Check to see if there are available rooms
            if (numOfRoomsAvailable <= 0 || bookedRooms > numOfRoomsAvailable) {
                return ResponseEntity.status(500).body("Could not Book Hotel. No Rooms Available!");
            } else {

                // update the hotel number of rooms available
                int numOfHotelRoomRemaining = numOfRoomsAvailable - bookedRooms;
                int updatedRow;
                updatedRow = hotelInventoryService.updateNumOfRoomsAvailable(booking.getHotelName(), numOfHotelRoomRemaining);

                int i = updatedRow;

                if (i == 0) {
                    return ResponseEntity.status(500).body("Could not Book Hotel. Please try again!");
                }

                booking.setBookingStatus("pending");
                Integer totalFare = booking.getNumOfRoomsBooked() * amountPerRoom;
                booking.setTotalPrice(totalFare);

                //create the booking
                bookingRepo.save(booking);


                //Handle the Payment Service call here
                BookingEvent bookingEvent = new BookingEvent();
                bookingEvent.setBookingId(booking.getBookingId());
                bookingEvent.setTotalFare(totalFare);
                bookingEvent.setUserId(booking.getUserId());

                redisTemplate.opsForValue().set(booking.getBookingId().toString(), "Payment In Progress");
                //Publish the booking event to Kafka
                bookingEventProducer.publishEvent(bookingEvent);
                return ResponseEntity.status(HttpStatus.CREATED).body("Booking added successfully");

            }

        }
    }

    public Optional<Booking> findBookingById(Long id)
    {
        return bookingRepo.findBookingByBookingId(id);
    }

    // This should be coming from Payment Service not from user

    public ResponseEntity<?> confirmBooking(Long bookingId) {
        logger.debug("Confirming Booking: " + bookingId);
        Optional<Booking> existingBooking = bookingRepo.findBookingByBookingId(bookingId);
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
            bookingRepo.save(booking); // Save the updated Booking
            logger.debug("Booking confirmed successfully: " + booking);
            return ResponseEntity.ok("Booking confirmed successfully");
        }
    }

    //@Transactional
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        logger.debug("Cancelling reservation: " + bookingId);
        Optional<Booking> existingBooking = bookingRepo.findBookingByBookingId(bookingId);
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
            bookingRepo.save(existingBooking.get());
            logger.debug("Booking cancelled successfully: " + bookingFound);
            return ResponseEntity.ok("Booking cancelled successfully");
        }
    }

}





